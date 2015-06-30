package com.adms.elearning.web.util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class MessageUtils {

	public static MessageUtils instance;
	
	public MessageUtils() {
		
	}
	
	public static MessageUtils getInstance() {
		if(instance == null) {
			instance = new MessageUtils();
		}
		return instance;
	}
	
	private FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}
	
	public void addInfoMessage(String clientId, String summary) {
		addInfoMessage(clientId, summary, "");
	}
	
	public void addInfoMessage(String clientId, String summary, String detail) {
		addMessage(clientId, infoMsg(summary, detail));
	}
	
	public void addErrorMessage(String clientId, String summary) {
		addMessage(clientId, errorMsg(summary, ""));
	}
	
	public void addErrorMessage(String clientId, String summary, String detail) {
		addMessage(clientId, errorMsg(summary, detail));
	}
	
	public void addWarnMessage(String clientId, String summary) {
		addMessage(clientId, warnMsg(summary, ""));
	}
	
	public void addWarnMessage(String clientId, String summary, String detail) {
		addMessage(clientId, warnMsg(summary, detail));
	}
	
	public void addFatalMessage(String clientId, String summary) {
		addMessage(clientId, fatalMsg(summary, ""));
	}
	
	public void addFatalMessage(String clientId, String summary, String detail) {
		addMessage(clientId, fatalMsg(summary, detail));
	}
	
	public void addMessage(String clientId, FacesMessage facesMessage) {
		getFacesContext().addMessage(clientId, facesMessage);
	}
	
	public FacesMessage getErrorFacesMsg(String summary, String detail) {
		return errorMsg(summary, detail);
	}
	
//	private method
	private FacesMessage infoMsg(String summary, String detail) {
		return new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
	}
	
	private FacesMessage errorMsg(String summary, String detail) {
		return new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail);
	}
	
	private FacesMessage warnMsg(String summary, String detail) {
		return new FacesMessage(FacesMessage.SEVERITY_WARN, summary, detail);
	}
	
	private FacesMessage fatalMsg(String summary, String detail) {
		return new FacesMessage(FacesMessage.SEVERITY_FATAL, summary, detail);
	}
	
}
