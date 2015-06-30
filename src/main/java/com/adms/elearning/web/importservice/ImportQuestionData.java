package com.adms.elearning.web.importservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import com.adms.elearning.entity.Answer;
import com.adms.elearning.entity.AnswerType;
import com.adms.elearning.entity.ClassRoom;
import com.adms.elearning.entity.Course;
import com.adms.elearning.entity.Question;
import com.adms.elearning.entity.Section;
import com.adms.elearning.service.AnswerService;
import com.adms.elearning.service.AnswerTypeService;
import com.adms.elearning.service.ClassRoomService;
import com.adms.elearning.service.CourseService;
import com.adms.elearning.service.QuestionService;
import com.adms.elearning.service.SectionService;
import com.adms.elearning.web.util.Utilize;

@Service
@Configurable
public class ImportQuestionData {

	private final String YES_STR = "Y";
	private final String NO_STR = "N";
	private final char[] alphabet = Utilize.getAlphabetAtoZ();
	private Map<String, AnswerType> _answerTypeMap;
	
	@Autowired
	private ClassRoomService classRoomService;
	
	@Autowired
	private CourseService courseService;
	
	@Autowired
	private SectionService sectionService;
	
	@Autowired
	private QuestionService questionService;
	
	@Autowired
	private AnswerService answerService;
	
	@Autowired
	private AnswerTypeService answerTypeService;
	
	public ImportQuestionData() {
		
	}
	
	@PostConstruct
	public void initial() {
		
	}

	private void initAnswerType() {
		try {
			List<AnswerType> list = answerTypeService.findAll();
			_answerTypeMap  = new HashMap<>();
			for(AnswerType a : list) {
				_answerTypeMap.put(a.getAnswerTypeCode(), a);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
//	public int importData(String fileName, byte[] contents, String contentType) throws Exception {
//		initAnswerType();
//		int result = 0;
//		
//		InputStream xls = null;
//		
//		InputStream format = Thread.currentThread().getContextClassLoader().getResourceAsStream("fileformat/question-format.xml");
//		
//		ExcelFormat ef = new ExcelFormat(format);
//		try {
//			xls = new ByteArrayInputStream(contents);
//			DataHolder wbHolder = ef.readExcel(xls);
//			
//			DataHolder sheetHolder = wbHolder.get(wbHolder.getSheetNameByIndex(0));
//			result = logic(sheetHolder.getDataList("dataList"));
//			
//		} catch (Exception e) {
//			throw e;
//		} finally {
//			try { xls.close(); } catch(Exception e) {}
//			try { format.close(); } catch(Exception e) {}
//		}
//		return result;
//	}
	
//	private int logic(List<DataHolder> dataList) throws Exception {
//		ClassRoom classRoom = null;
//		Course course = null;
//		Section section = null;
////		Question question = null;
//		
//		String campaignName = "";
//		
//		String sectionNo = "";
//		String sectionName = "";
//		String sectionDesc = "";
//		
//		String questionNo = "";
//		String questionTxt = "";
//		
//		String answer = "";
//		String choiceA = "";
//		String choiceB = "";
//		String choiceC = "";
//		String choiceD = "";
//		
//		int importResult = 0;
//		
//		Double totalRecords = Double.valueOf(dataList.size());
//		
//		for(DataHolder data : dataList) {
//			
//			if(!campaignName.equals(data.get("campaignName").getStringValue())) {
//				campaignName = data.get("campaignName").getStringValue();
//
//				//TODO Now, Class Room is same as Course
//				classRoom = saveClassRoom(campaignName);
//				
//				course = saveCourse(campaignName, classRoom);
//			}
//			
//			if(!sectionNo.equals(data.get("sectionNo").getStringValue())) {
//				sectionNo = data.get("sectionNo").getStringValue();
//				sectionName = data.get("sectionName").getStringValue();
//				sectionDesc = data.get("sectionDesc") != null ? data.get("sectionDesc").getStringValue() : "";
//				
//				section = saveSection(sectionNo, sectionName, sectionDesc, course);
//			}
//			
//			if(!questionNo.equals(data.get("questionNo").getStringValue())) {
//				questionNo = data.get("questionNo").getStringValue();
//				questionTxt = data.get("questionTxt").getStringValue();
//				
//				answer = data.get("correctAns").getStringValue();
//				choiceA = data.get("choiceA") != null ? data.get("choiceA").getStringValue() : "";
//				choiceB = data.get("choiceB") != null ? data.get("choiceB").getStringValue() : "";
//				choiceC = data.get("choiceC") != null ? data.get("choiceC").getStringValue() : "";
//				choiceD = data.get("choiceD") != null ? data.get("choiceD").getStringValue() : "";
//				
//				saveAnswer(section, questionNo, questionTxt, answer, new String[]{choiceA, choiceB, choiceC, choiceD});
//			}
//			++importResult;
//		}
//		return importResult;
//	}
	
	public ClassRoom saveClassRoom(String campaignName, String loginBy) throws Exception {
		ClassRoom c = new ClassRoom();
		c.setClassCode(campaignName);
		
		List<ClassRoom> list = classRoomService.find(c);
		
		if(!list.isEmpty() && list.size() == 1) {
			c = list.get(0);
		} else if(list.isEmpty()) {
			c.setClassName(campaignName);
			c = classRoomService.add(c, loginBy);
		} else {
			throw new Exception("Found Error for: " + c.toString());
		}
		return c;
	}
	
	public Course saveCourse(String courseName, ClassRoom classRoom, String loginBy) throws Exception {
		Course c = new Course();
		c.setCourseName(courseName);
		
		List<Course> list = courseService.find(c);
		
		if(!list.isEmpty() && list.size() == 1) {
			c = list.get(0);
		} else if(list.isEmpty()) {
			c.setCourseDescription(courseName);
			c.setClassRoom(classRoom);
			c = courseService.add(c, loginBy);
		} else {
			throw new Exception("Found Error for: " + c.toString());
		}
		return c;
	}
	
	public Section saveSection(String sectionNo, String sectionName, String sectionDesc, Course course, String loginBy) throws Exception {
		Section s = new Section();
		s.setSectionNo(Integer.valueOf(sectionNo));
		List<Section> list = sectionService.findByNamedQuery("getSectionByCourseIdAndSectionNo", course.getId(), s.getSectionNo());
		
		if(!list.isEmpty() && list.size() == 1) {
			Section check = list.get(0);
			boolean flag = false;
			
			if(StringUtils.isNoneBlank(check.getSectionName())) {
				if(!check.getSectionName().equals(sectionName == null ? "" : sectionName)) {
					flag = true;
					check.setSectionName(sectionName);
				}
			}
			
			if(StringUtils.isNoneBlank(check.getSectionDescription())) {
				if(!check.getSectionDescription().equals(sectionDesc == null ? "" : sectionDesc)) {
					flag = true;
					check.setSectionDescription(sectionDesc);
				}
			}
			
			if(flag) {
				s = sectionService.update(check, loginBy);
			} else {
				s = list.get(0);
			}
			check = null;
		} else if(list.isEmpty()) {
			s.setSectionName(sectionName);
			s.setSectionDescription(sectionDesc);
			s.setCourse(course);
			s = sectionService.add(s, loginBy);
		} else {
			throw new Exception("Found Error for: " + s.toString());
		}
		return s;
	}
	
	public Question saveQuestion(String questionNo, String questionTxt, Section section, boolean isForceAddNew, String loginBy) throws Exception {
		Question q = new Question();
		q.setQuestionNo(Integer.valueOf(questionNo));
		q.setQuestionText(questionTxt);
		
		List<Question> list = questionService.findByNamedQuery("getQuestionByCourseIdAndSectionNoAndQuestionNo"
				, section.getCourse().getId(), section.getSectionNo(), q.getQuestionNo());
		
		if(!list.isEmpty() && list.size() == 1) {
			Question check = list.get(0);
			if(!check.getQuestionText().equals(questionTxt) || isForceAddNew) {
				check.setActive(NO_STR);
				check = questionService.update(check, loginBy);
				
				q = addQuestion(q.getQuestionNo(), questionTxt, check.getSection(), loginBy);
			} else {
				q = list.get(0);
			}
		} else if(list.isEmpty()) {
			q = addQuestion(q.getQuestionNo(), questionTxt, section, loginBy);
		} else {
			throw new Exception("Found Error for: " + q.toString());
		}
		
		return q;
	}
	
	private Question addQuestion(Integer questionNo, String questionText, Section section, String loginBy) throws Exception {
		Question q = new Question();
		q.setQuestionNo(questionNo);
		q.setQuestionText(questionText);
		q.setActive(YES_STR);
		q.setSection(section);
		return questionService.add(q, loginBy);
	}
	
	public void saveAnswer(Section section, String questionNo, String questionTxt, String correctAnswer, String loginBy, String...choices) throws Exception {
		Answer answer = null;
		Answer check = null;

		initAnswerType();
		
		List<Answer> answers = new ArrayList<>();

//		Initial new choices before checking process
		int i = 0;
		for(String c : choices) {
			if(StringUtils.isNoneBlank(c)) {
				String cAns = String.valueOf(alphabet[i]).toUpperCase();
				answer = new Answer();
				answer.setAnswerText(c);
				answer.setChoiceLetter(cAns);
				answer.setAnswerType(_answerTypeMap.get(cAns.equals(correctAnswer) ? "CORRECT_ANS" : "WRONG_ANS"));
				answers.add(answer);
				i++;
			}
		}
		
//		Get All choices by Course, Section, Question
//		System.out.println("section.getCourse().getId(): " + section.getCourse().getId());
//		System.out.println("section.getId(): " + section.getId());
//		System.out.println("Long.parseLong(questionNo): " + Long.parseLong(questionNo));
		
		/*
		 * Cannot use NamedQuery, still don't know why.
		 * So, query by Hibernate criteria instead.
		 * query="SELECT t.ID, t.QUESTION, t.CHOICE_LETTER, t.ANSWER_TYPE, t.ANSWER_TEXT "
				+ " FROM ANSWER t "
				+ "	LEFT JOIN ANSWER_TYPE at on t.ANSWER_TYPE = at.ANSWER_TYPE_CODE "
				+ " LEFT JOIN QUESTION q on t.QUESTION = q.ID "
				+ " LEFT JOIN SECTION s on q.SECTION = s.ID "
				+ " LEFT JOIN COURSE c on s.COURSE = c.ID "
				+ " WHERE q.ACTIVE = 'Y' "
				+ " AND c.ID = ? "
				+ " AND s.SECTION_NO = ? "
				+ " AND q.QUESTION_NO = ? "
		 */
		
		DetachedCriteria ansCriteria = DetachedCriteria.forClass(Answer.class);
		
		DetachedCriteria questionCriteria = ansCriteria.createCriteria("question");
		questionCriteria.add(Restrictions.eq("questionNo", Integer.parseInt(questionNo)));
		questionCriteria.add(Restrictions.eq("active", "Y"));
		
		DetachedCriteria sectionCriteria = questionCriteria.createCriteria("section");
		sectionCriteria.add(Restrictions.eq("id", section.getId()));
		
		DetachedCriteria courseCriteria = sectionCriteria.createCriteria("course");
		courseCriteria.add(Restrictions.eq("id", section.getCourse().getId()));
		
		List<Answer> list = answerService.findByCriteria(ansCriteria);
//		List<Answer> list = answerService.findByNamedQuery("getAnswerByCourseIdSectionNoAndQuestionNo", section.getCourse().getId(), section.getId(), Integer.parseInt(questionNo));
		
		if(!list.isEmpty()) {
//			checking answer text process
			int n = 0;
			boolean isDiff = false;
			for(Answer ans : list) {
				check = answers.get(n);
				if(!ans.getAnswerText().equals(check.getAnswerText())) {
					isDiff = true;
					break;
				}
				n++;
			}
			
			if(isDiff) {
//				insert all new Question and answer choices to DB
				addQuestionAndAnswerChoices(questionNo, questionTxt, section, answers, true, loginBy);
			}
			
		} else {
//			Choice is null, Add all to DB
			addQuestionAndAnswerChoices(questionNo, questionTxt, section, answers, false, loginBy);
		}
	}
	
	private void addQuestionAndAnswerChoices(String questionNo, String questionTxt, Section section, List<Answer> answers, boolean isForceAdd, String loginBy) throws Exception {
		Question question = saveQuestion(questionNo, questionTxt, section, isForceAdd, loginBy);
		for(Answer ans : answers) {
			ans.setQuestion(question);
			answerService.add(ans, loginBy);
		}
	}

	public void setClassRoomService(ClassRoomService classRoomService) {
		this.classRoomService = classRoomService;
	}

	public void setCourseService(CourseService courseService) {
		this.courseService = courseService;
	}
	
	public void setSectionService(SectionService sectionService) {
		this.sectionService = sectionService;
	}
	
	public void setQuestionService(QuestionService questionService) {
		this.questionService = questionService;
	}

	public void setAnswerService(AnswerService answerService) {
		this.answerService = answerService;
	}

	public void setAnswerTypeService(AnswerTypeService answerTypeService) {
		this.answerTypeService = answerTypeService;
	}

}
