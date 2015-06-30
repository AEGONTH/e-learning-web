package com.adms.elearning.web.model;

import java.util.List;

import javax.faces.model.SelectItem;

public class QuestionModel {

	private Long questionId;
	private String questionText;
	private List<SelectItem> choices;
	private Long answer;
	
	public QuestionModel() {
		
	}
	
	public String getQuestionText() {
		return questionText;
	}
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}
	public List<SelectItem> getChoices() {
		return choices;
	}
	public void setChoices(List<SelectItem> choices) {
		this.choices = choices;
	}
	public Long getAnswer() {
		return answer;
	}
	public void setAnswer(Long answer) {
		this.answer = answer;
	}

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}
	
}
