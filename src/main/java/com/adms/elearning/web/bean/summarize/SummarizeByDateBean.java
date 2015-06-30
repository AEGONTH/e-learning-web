package com.adms.elearning.web.bean.summarize;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.adms.elearning.entity.CourseEnrolment;

public class SummarizeByDateBean implements Serializable {

	private static final long serialVersionUID = 6245319472796022294L;

	private Date date;
	
	private List<CourseEnrolment> candidates;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<CourseEnrolment> getCandidates() {
		return candidates;
	}

	public void setCandidates(List<CourseEnrolment> candidates) {
		this.candidates = candidates;
	}

	@Override
	public String toString() {
		return "SummarizeByDateBean [date=" + date + ", candidates="
				+ candidates.size() + "]";
	}
	
}
