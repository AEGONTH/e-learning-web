package com.adms.elearning.web.bean.admin;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.adms.elearning.web.bean.base.BaseBean;

@ManagedBean
@SessionScoped
public class AdminSession extends BaseBean {

	private static final long serialVersionUID = -3686543879592889638L;
	private String token;
	private Date signInDate;

	public AdminSession() {

	}

	@PostConstruct
	private void init() {
		token = "";
		signInDate = null;
	}

	public void clearState() {
		init();
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getSignInDate() {
		return signInDate;
	}

	public void setSignInDate(Date signInDate) {
		this.signInDate = signInDate;
	}

}
