package com.adms.elearning.web.bean.courseinfo;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.adms.elearning.entity.Course;
import com.adms.elearning.entity.CourseEnrolment;
import com.adms.elearning.entity.ExamLevel;
import com.adms.elearning.entity.ExamType;
import com.adms.elearning.entity.Section;
import com.adms.elearning.entity.Student;
import com.adms.elearning.service.CourseEnrolmentService;
import com.adms.elearning.service.CourseService;
import com.adms.elearning.service.ExamLevelService;
import com.adms.elearning.service.ExamTypeService;
import com.adms.elearning.service.SectionService;
import com.adms.elearning.service.StudentService;
import com.adms.elearning.web.bean.base.BaseBean;
import com.adms.elearning.web.bean.login.LoginSession;
import com.adms.elearning.web.util.MessageUtils;
import com.adms.utils.DateUtil;

@ManagedBean
@ViewScoped
public class CourseInfoView extends BaseBean {

	private static final long serialVersionUID = 5165374821258297160L;

	private List<SelectItem> selectionLevels;
	private List<SelectItem> selectionCampaigns;
	private List<SelectItem> selectionSections;

	private String selectedLevel;
	private String selectedCampaign;
	private String selectedSection;

	@ManagedProperty(value="#{loginSession}")
	private LoginSession loginSession;

	@ManagedProperty(value="#{courseService}")
	private CourseService courseService;

	@ManagedProperty(value="#{examLevelService}")
	private ExamLevelService examLevelService;

	@ManagedProperty(value="#{examTypeService}")
	private ExamTypeService examTypeService;

	@ManagedProperty(value="#{courseEnrolmentService}")
	private CourseEnrolmentService courseEnrolmentService;

	@ManagedProperty(value="#{studentService}")
	private StudentService studentService;

	@ManagedProperty(value="#{sectionService}")
	private SectionService sectionService;

	public CourseInfoView() {

	}

	@PostConstruct
	private void init()	{
		selectionLevels = new ArrayList<>();
		selectionCampaigns = new ArrayList<>();
		try {

			selectionLevels.add(new SelectItem("", super.getGlobalMsgValue("common.txt.please.select")));
			List<ExamLevel> examLevels = examLevelService.findAll();
			for(ExamLevel e : examLevels) {
				selectionLevels.add(new SelectItem(e.getId(), e.getExamLevel()));
			}

			selectionCampaigns.add(new SelectItem("", super.getGlobalMsgValue("common.txt.please.select")));
			List<Course> courses = courseService.findAll();
			for(Course course : courses) {
				selectionCampaigns.add(new SelectItem(course.getId(), course.getCourseName()));
			}

			selectionSectionInitial(null);

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void selectionSectionInitial(List<Section> sections) {
		selectionSections = new ArrayList<SelectItem>();
//		selectionSections.add(new SelectItem("", super.getGlobalMsgValue("common.txt.select.all")));
		selectionSections.add(new SelectItem("", super.getGlobalMsgValue("common.txt.please.select")));

		if(sections != null && !sections.isEmpty()) {
			for(Section section : sections) {
				String label = section.getSectionNo() + ".) " + section.getSectionName().trim();
				selectionSections.add(new SelectItem(section.getId(), label, section.getSectionDescription()));
			}
		}

	}

	public void courseSelectListener() {
		try {
			if(StringUtils.isBlank(selectedCampaign)) {
				selectionSectionInitial(null);
			} else {
				DetachedCriteria criteria = DetachedCriteria.forClass(Section.class);
				criteria.add(Restrictions.eq("course.id", Long.parseLong(selectedCampaign)));
				criteria.addOrder(Order.asc("sectionNo"));
				selectionSectionInitial(sectionService.findByCriteria(criteria));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public String proceed() {
		try {
			boolean flag = false;

			if(StringUtils.isNotBlank(selectedCampaign)
					&& StringUtils.isNotBlank(selectedLevel)
					&& StringUtils.isNotBlank(selectedSection)) {
				flag = true;
				loginSession.setCampaignId(selectedCampaign);
				loginSession.setLevelId(selectedLevel);
				loginSession.setSectionId(selectedSection);
			} else {
				if(StringUtils.isBlank(selectedLevel)) {
					((UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmMain:selectionLevel")).setValid(false);
					MessageUtils.getInstance().addErrorMessage("msg", super.getGlobalMsgValue("validate.err.level.required"));
				}

				if(StringUtils.isBlank(selectedCampaign)) {
					((UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmMain:selectionCampaign")).setValid(false);
					MessageUtils.getInstance().addErrorMessage("msg", super.getGlobalMsgValue("validate.err.campaign.required"));
				}

				if(StringUtils.isBlank(selectedSection)) {
					((UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmMain:selectionSection")).setValid(false);
					MessageUtils.getInstance().addErrorMessage("msg", super.getGlobalMsgValue("validate.err.section.required"));
				}

			}

			if(flag) {
				CourseEnrolment ce = retrieveCourseEnrolment(studentService.find(new Student(loginSession.getUserLogin().getUser())).get(0)
						, courseService.findById(Long.parseLong(selectedCampaign))
						, examLevelService.findById(Long.parseLong(selectedLevel))
						, StringUtils.isBlank(selectedSection) ? null : sectionService.findById(Long.parseLong(selectedSection))
						, examTypeService.findAll().get(0));
				loginSession.setCourseEnrolment(ce);
				FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("courseEnrolmentId", ce.getId());
				return "question/questions?faces-redirect=true";
			}

		} catch(Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private CourseEnrolment retrieveCourseEnrolment(Student student, Course course, ExamLevel examLevel, Section section, ExamType examType) throws Exception {
////		<!-- Create Criteria -->
//		DetachedCriteria courseEnrolCriteria = DetachedCriteria.forClass(CourseEnrolment.class);
////		<!-- Join Student -->
//		DetachedCriteria studentCriteria = courseEnrolCriteria.createCriteria("student", JoinType.INNER_JOIN);
//		studentCriteria.add(Restrictions.eq("id", student.getId()));
////		<!-- join Exam Level -->
//		DetachedCriteria examLvCriteria = courseEnrolCriteria.createCriteria("examLevel", JoinType.INNER_JOIN);
//		examLvCriteria.add(Restrictions.eq("examLevelCode", examLevel.getExamLevelCode()));
////		<!-- Join Exam Type -->
//		DetachedCriteria examTypeCriteria = courseEnrolCriteria.createCriteria("examType", JoinType.INNER_JOIN);
//		examTypeCriteria.add(Restrictions.eq("examTypeCode", examType.getExamTypeCode()));
////		<!-- Join Course -->
//		DetachedCriteria courseCriteria = courseEnrolCriteria.createCriteria("course", JoinType.INNER_JOIN);
//		courseCriteria.add(Restrictions.eq("id", course.getId()));
//
//		courseEnrolmentService.findByCriteria(courseEnrolCriteria);

		CourseEnrolment example = new CourseEnrolment();
		example.setStudent(student);
		example.setCourse(course);
		example.setExamLevel(examLevel);
		example.setSection(section);
		example.setExamType(examType);
		example.setExamDate(DateUtil.getCurrentDate());

		example = courseEnrolmentService.add(example, SYSTEM_LOG_BY);

		return example;
	}

	public List<SelectItem> getSelectionLevels() {
		return selectionLevels;
	}

	public void setSelectionLevels(List<SelectItem> selectionLevels) {
		this.selectionLevels = selectionLevels;
	}

	public List<SelectItem> getSelectionCampaigns() {
		return selectionCampaigns;
	}

	public void setSelectionCampaigns(List<SelectItem> selectionCampaigns) {
		this.selectionCampaigns = selectionCampaigns;
	}

	public List<SelectItem> getSelectionSections() {
		return selectionSections;
	}

	public void setSelectionSections(List<SelectItem> selectionSections) {
		this.selectionSections = selectionSections;
	}

	public String getSelectedLevel() {
		return selectedLevel;
	}

	public void setSelectedLevel(String selectedLevel) {
		this.selectedLevel = selectedLevel;
	}

	public String getSelectedCampaign() {
		return selectedCampaign;
	}

	public void setSelectedCampaign(String selectedCampaign) {
		this.selectedCampaign = selectedCampaign;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}

	public CourseEnrolmentService getCourseEnrolmentService() {
		return courseEnrolmentService;
	}

	public void setCourseEnrolmentService(
			CourseEnrolmentService courseEnrolmentService) {
		this.courseEnrolmentService = courseEnrolmentService;
	}

	public void setLoginSession(LoginSession loginSession) {
		this.loginSession = loginSession;
	}

	public void setCourseService(CourseService courseService) {
		this.courseService = courseService;
	}

	public void setExamLevelService(ExamLevelService examLevelService) {
		this.examLevelService = examLevelService;
	}

	public void setExamTypeService(ExamTypeService examTypeService) {
		this.examTypeService = examTypeService;
	}

	public void setStudentService(StudentService studentService) {
		this.studentService = studentService;
	}

	public void setSectionService(SectionService sectionService) {
		this.sectionService = sectionService;
	}

}
