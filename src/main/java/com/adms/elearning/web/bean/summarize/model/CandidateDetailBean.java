package com.adms.elearning.web.bean.summarize.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.adms.elearning.entity.Course;
import com.adms.elearning.entity.CourseResult;
import com.adms.elearning.entity.ExamLevel;
import com.adms.elearning.entity.Student;

public class CandidateDetailBean implements Serializable {

	private static final long serialVersionUID = -6092154700636994358L;

	private Date examDate;
	
	private Student student;
	
	private Course course;
	
	private ExamLevel examLevel;
	
	private List<CourseResult> courseResults;
	
	private List<DetailGroupBySection> bySection;
	
	public CandidateDetailBean(Date examDate, Student student, Course course, ExamLevel examLevel, List<CourseResult> courseResults) {
		this.examDate = examDate;
		this.student = student;
		this.course = course;
		this.examLevel = examLevel;
		this.courseResults = courseResults;
		
		initBySection(courseResults);
	}
	
	private void initBySection(List<CourseResult> courseResults) {
		bySection = new ArrayList<>();
		DetailGroupBySection detail = null;
		Integer marksBySection = 0;
		
		for(CourseResult data : courseResults) {
			
			if(detail == null || detail.getSectionId() != data.getAnswer().getQuestion().getSection().getId()) {
				if(detail != null) bySection.add(detail);
				
				detail = new DetailGroupBySection();
				detail.setSectionId(data.getAnswer().getQuestion().getSection().getId());
				detail.setSectionName(data.getAnswer().getQuestion().getSection().getSectionName());
				detail.setDescription(data.getAnswer().getQuestion().getSection().getSectionDescription());
				detail.setSectionNo(data.getAnswer().getQuestion().getSection().getSectionNo());
				detail.setResults(new ArrayList<CourseResult>());
				marksBySection = 0;
			}
			if(data.getAnswer().getAnswerType().getAnswerTypeCode().toUpperCase().equals("CORRECT_ANS")) marksBySection++;
			detail.setMarks(marksBySection);
			detail.getResults().add(data);
		}
		if(detail != null) bySection.add(detail);
	}

	public Date getExamDate() {
		return examDate;
	}

	public void setExamDate(Date examDate) {
		this.examDate = examDate;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public ExamLevel getExamLevel() {
		return examLevel;
	}

	public void setExamLevel(ExamLevel examLevel) {
		this.examLevel = examLevel;
	}

	public List<CourseResult> getCourseResults() {
		return courseResults;
	}

	public void setCourseResults(List<CourseResult> courseResults) {
		this.courseResults = courseResults;
	}

	public List<DetailGroupBySection> getBySection() {
		return bySection;
	}

	public void setBySection(List<DetailGroupBySection> bySection) {
		this.bySection = bySection;
	}

}
