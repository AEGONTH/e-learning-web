package com.adms.elearning.web.bean.login;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.adms.common.entity.UserLogin;
import com.adms.elearning.entity.CourseEnrolment;
import com.adms.elearning.web.bean.base.BaseBean;

@ManagedBean
@SessionScoped
public class LoginSession extends BaseBean {

	private static final long serialVersionUID = -4511395913544909376L;

	private UserLogin userLogin;
	private String campaignId;
	private String levelId;
	private String sectionId;

	private CourseEnrolment courseEnrolment;

	public UserLogin getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(UserLogin userLogin) {
		this.userLogin = userLogin;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public String getLevelId() {
		return levelId;
	}

	public void setLevelId(String levelId) {
		this.levelId = levelId;
	}

	public CourseEnrolment getCourseEnrolment() {
		return courseEnrolment;
	}

	public void setCourseEnrolment(CourseEnrolment courseEnrolment) {
		this.courseEnrolment = courseEnrolment;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

}
