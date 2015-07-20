package com.adms.elearning.web.bean.login;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.adms.common.entity.UserLogin;
import com.adms.elearning.entity.CourseEnrolment;
import com.adms.elearning.service.CourseEnrolmentService;
import com.adms.elearning.web.bean.base.BaseBean;

@ManagedBean
@SessionScoped
public class LoginSession extends BaseBean {

	private static final long serialVersionUID = -4511395913544909376L;

	private UserLogin userLogin;
	private String campaignId;
	private String levelId;
	private String sectionId;

	private boolean finished;

	private CourseEnrolment courseEnrolment;

	@ManagedProperty(value="#{courseEnrolmentService}")
	private CourseEnrolmentService courseEnrolmentService;

	@PostConstruct
	private void init() {
		finished = false;
	}

	public void signOut() throws IOException {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		ec.invalidateSession();
		ec.redirect(ec.getRequestContextPath() + "/login.jsf");
	}

	public Integer[] getScoreResult() {
		Integer[] results = new Integer[2];
		try {
			if(isFinished()) {
				CourseEnrolment ce = courseEnrolmentService.find(courseEnrolment.getId());
				results[0] = ce.getMarks();
				results[1] = ce.getFullMarks();
			} else {
				signOut();
			}
		} catch(Exception e) {
			try {
				signOut();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		return results;
	}

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

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public void setCourseEnrolmentService(CourseEnrolmentService courseEnrolmentService) {
		this.courseEnrolmentService = courseEnrolmentService;
	}

}
