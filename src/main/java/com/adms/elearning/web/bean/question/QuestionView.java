package com.adms.elearning.web.bean.question;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import com.adms.elearning.entity.Answer;
import com.adms.elearning.entity.CourseResult;
import com.adms.elearning.entity.Question;
import com.adms.elearning.service.AnswerService;
import com.adms.elearning.service.CourseEnrolmentService;
import com.adms.elearning.service.CourseResultService;
import com.adms.elearning.service.QuestionService;
import com.adms.elearning.web.bean.base.BaseBean;
import com.adms.elearning.web.bean.login.LoginSession;
import com.adms.elearning.web.model.QuestionModel;
import com.adms.elearning.web.model.SectionModel;
import com.adms.elearning.web.util.MessageUtils;

@ManagedBean
@ViewScoped
public class QuestionView extends BaseBean {

	private static final long serialVersionUID = 6633083012737219880L;

	private List<SectionModel> sectionModels;
	private int currSectionNum;

	private int currQuestionNum;

	private boolean sectionIntro;

	@ManagedProperty(value="#{loginSession}")
	private LoginSession loginSession;

	@ManagedProperty(value="#{questionService}")
	private QuestionService questionService;

	@ManagedProperty(value="#{answerService}")
	private AnswerService answerService;

	@ManagedProperty(value="#{courseResultService}")
	private CourseResultService courseResultService;

	@ManagedProperty(value="#{courseEnrolmentService}")
	private CourseEnrolmentService courseEnrolmentService;

	private Map<Long, Answer> answerMap;

	public QuestionView() {

	}

	@PostConstruct
	public void init() {
		answerMap = new HashMap<>();

		sectionIntro = true;
		currSectionNum = 0;
		sectionModels = new ArrayList<>();

		SectionModel sectionModel = null;
		List<QuestionModel> questionModels = null;
		QuestionModel qm = null;

		currQuestionNum = 0;

//		<!-- Initial Questions -->
		try {
			DetachedCriteria questionDC = DetachedCriteria.forClass(Question.class);
			DetachedCriteria sectionDC = questionDC.createCriteria("section", JoinType.INNER_JOIN);
			sectionDC.createCriteria("course", JoinType.INNER_JOIN).add(Restrictions.eq("id", Long.parseLong(loginSession.getCampaignId())));

			sectionDC.add(Restrictions.eq("id", Long.parseLong(loginSession.getSectionId())));

			questionDC.add(Restrictions.eq("active", "Y"));

			sectionDC.addOrder(Order.asc("sectionNo"));
			questionDC.addOrder(Order.asc("questionNo"));

			List<Question> questions = questionService.findByCriteria(questionDC);

			if(questions == null || questions.isEmpty()) {
				MessageUtils.getInstance().addErrorMessage(null, "ERR-001: " + super.getGlobalMsgValue("ERR.001"));
				return;
			}

			for(Question question : questions) {
				if(sectionModel == null || (sectionModel != null && sectionModel.getSectionId().compareTo(question.getSection().getId()) != 0)) {
					if(sectionModel != null) {
						sectionModel.setQuestions(questionModels);
						sectionModels.add(sectionModel);
					}
					sectionModel = new SectionModel();
					sectionModel.setSectionId(question.getSection().getId());
					sectionModel.setSectionName(question.getSection().getSectionName());
					sectionModel.setSectionDesc(question.getSection().getSectionDescription());

					questionModels = new ArrayList<>();
				}

				qm = new QuestionModel();
				qm.setQuestionId(question.getId());
				qm.setQuestionText(question.getQuestionText());
				qm.setChoices(retrieveAnswerChoicesByQuestionId(question.getId()));

				questionModels.add(qm);
			}
			if(sectionModel != null) {
				sectionModel.setQuestions(questionModels);
				sectionModels.add(sectionModel);
			}

		} catch(Exception e) {
			try {
				loginSession.signOut();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	private List<SelectItem> retrieveAnswerChoicesByQuestionId(Long questionId) throws Exception {
		List<SelectItem> choices = new ArrayList<>();

		DetachedCriteria answerDC = DetachedCriteria.forClass(Answer.class);
		answerDC.createCriteria("question", JoinType.INNER_JOIN).add(Restrictions.eq("id", questionId));
		answerDC.addOrder(Order.asc("choiceLetter"));
		List<Answer> answers = answerService.findByCriteria(answerDC);

		for(Answer a : answers) {
			choices.add(new SelectItem(a.getId(), " " + a.getChoiceLetter() + ") " + a.getAnswerText()));
			answerMap.put(a.getId(), a);
		}

		return choices;
	}

	public String doFinish() throws Exception {
		if(validateQuestionAnswer()) {
			try {
				saveAnswer();
				loginSession.setFinished(true);
				FacesContext.getCurrentInstance()
					.getExternalContext()
					.redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/page/final.jsf");
				return "/page/final.xhtml?faces-redirect=true";
			} catch(Exception e) {
				throw e;
			}
		}
		return null;
	}

	public void startQuestion() {
		sectionIntro = false;
	}

	public void nextSection() throws Exception {
		if(validateQuestionAnswer()) {
			try {
				saveAnswer();
				logicNextSection();
			} catch(Exception e) {
				MessageUtils.getInstance().addErrorMessage(null, e.getMessage());
				throw e;
			}
		}
	}

	public void nextQuestion() throws Exception {
		if(validateQuestionAnswer()) {
			try {
				saveAnswer();
				currQuestionNum++;
			} catch(Exception e) {
				MessageUtils.getInstance().addErrorMessage(null, e.getMessage());
				throw e;
			}
		}
	}

	public void previousQuestion() throws Exception {
		saveAnswer();
		currQuestionNum--;
	}

	private CourseResult saveAnswer() throws Exception {
//		Must do before changing question process
		CourseResult courseResult = null;
		QuestionModel q = sectionModels.get(currSectionNum).getQuestions().get(currQuestionNum);

		if(q.getAnswer() == null) {
			return null;
		}
		Answer answer = answerMap.get(q.getAnswer());

		DetachedCriteria criteria = DetachedCriteria.forClass(CourseResult.class);
		criteria.createCriteria("courseEnrolment").add(Restrictions.eq("id", loginSession.getCourseEnrolment().getId()));
		criteria.createCriteria("answer").add(Restrictions.eq("id", answer.getId()));

		List<CourseResult> list = courseResultService.findByCriteria(criteria);
		if(list == null || list.isEmpty() || (list != null && list.size() == 0)) {
			courseResult = courseResultService.add(new CourseResult(loginSession.getCourseEnrolment(), answer), SYSTEM_LOG_BY);
		} else if(list.size() == 1) {
			courseResult = list.get(0);
			if(courseResult.getAnswer().getId() != answer.getId()) {
				courseResult.setAnswer(answer);
				courseResult = courseResultService.update(courseResult, SYSTEM_LOG_BY);
			}
		} else {
			throw new Exception("Found Course Result more than 1 => CourseEnrolment ID: " + loginSession.getCourseEnrolment().getId() + " | Answer ID: " + answer.getId());
		}
		return courseResult;
	}

	private boolean validateQuestionAnswer() {
		QuestionModel q = sectionModels.get(currSectionNum).getQuestions().get(currQuestionNum);
//		q.setAnswer(answerId);
		if(q.getAnswer() == null) {
			MessageUtils.getInstance().addErrorMessage("msg", getGlobalMsgValue("validate.err.question.answer"));
			return false;
		}
		return true;
	}

	private void logicNextSection() {
		currSectionNum++;
		currQuestionNum = 0;
		sectionIntro = true;

	}

	public int getCurrQuestionNum() {
		return currQuestionNum;
	}

	public void setCurrQuestionNum(int currQuestionNum) {
		this.currQuestionNum = currQuestionNum;
	}

	public List<SectionModel> getSectionModels() {
		return sectionModels;
	}

	public void setSectionModels(List<SectionModel> sectionModels) {
		this.sectionModels = sectionModels;
	}

	public int getCurrSectionNum() {
		return currSectionNum;
	}

	public void setCurrSectionNum(int currSectionNum) {
		this.currSectionNum = currSectionNum;
	}

	public boolean isSectionIntro() {
		return sectionIntro;
	}

	public void setSectionIntro(boolean sectionIntro) {
		this.sectionIntro = sectionIntro;
	}

	public void setLoginSession(LoginSession loginSession) {
		this.loginSession = loginSession;
	}

	public void setQuestionService(QuestionService questionService) {
		this.questionService = questionService;
	}

	public void setAnswerService(AnswerService answerService) {
		this.answerService = answerService;
	}

	public void setCourseResultService(CourseResultService courseResultService) {
		this.courseResultService = courseResultService;
	}

	public void setCourseEnrolmentService(CourseEnrolmentService courseEnrolmentService) {
		this.courseEnrolmentService = courseEnrolmentService;
	}
}
