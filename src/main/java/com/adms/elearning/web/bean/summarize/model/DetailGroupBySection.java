package com.adms.elearning.web.bean.summarize.model;

import java.util.List;

import com.adms.elearning.entity.CourseResult;

public class DetailGroupBySection {

	private Long sectionId;
	
	private Integer sectionNo;

	private String sectionName;
	
	private String description;
	
	private List<CourseResult> results;
	
	private Integer marks;

	public Long getSectionId() {
		return sectionId;
	}

	public void setSectionId(Long sectionId) {
		this.sectionId = sectionId;
	}

	public Integer getSectionNo() {
		return sectionNo;
	}

	public void setSectionNo(Integer sectionNo) {
		this.sectionNo = sectionNo;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<CourseResult> getResults() {
		return results;
	}

	public void setResults(List<CourseResult> results) {
		this.results = results;
	}

	public Integer getMarks() {
		return marks;
	}

	public void setMarks(Integer marks) {
		this.marks = marks;
	}
	
}
