package com.adms.elearning.web.model;

import java.util.List;

public class SectionModel {

	private Long sectionId;
	private String sectionName;
	private String sectionDesc;
	private List<QuestionModel> questions;
	
	public Long getSectionId() {
		return sectionId;
	}
	public void setSectionId(Long sectionId) {
		this.sectionId = sectionId;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public String getSectionDesc() {
		return sectionDesc;
	}
	public void setSectionDesc(String sectionDesc) {
		this.sectionDesc = sectionDesc;
	}
	public List<QuestionModel> getQuestions() {
		return questions;
	}
	public void setQuestions(List<QuestionModel> questions) {
		this.questions = questions;
	}
}
