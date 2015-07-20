package com.adms.elearning.web.authorization;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.servlet.http.HttpSession;

public class AuthorizationListener extends PhaseAuthorization {

	private static final long serialVersionUID = 6201046093069477711L;

	@Override
	public void afterPhase(PhaseEvent event) {
		FacesContext facesContext = event.getFacesContext();

		String currentPage = facesContext.getViewRoot().getViewId();
		System.out.println("phaseID:" + event.getPhaseId() + " | after: " + currentPage);

		boolean isExceptionalPage = (
				currentPage.toLowerCase().contains("index")
				|| currentPage.toLowerCase().contains("admin")
				|| currentPage.toLowerCase().contains("error")
				|| currentPage.toLowerCase().contains("final"));

		boolean isLoginPage = (currentPage.lastIndexOf("login.")) > -1;

		if(isExceptionalPage) {
			return;
		}

		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);

		if(session == null) {
			try {
				super.redirectToLogin(facesContext);
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			Object currentUser = session.getAttribute("username");
//			System.out.println("Object currentUser: " + currentUser);
			if(!isLoginPage && (currentUser == null || currentUser == "")) {
				try {
					super.redirectToLogin(facesContext);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void beforePhase(PhaseEvent event) {
		FacesContext facesContext = event.getFacesContext();
		if(facesContext.getViewRoot() != null) {
			String currentPage = facesContext.getViewRoot().getViewId();
			System.out.println("before: " + currentPage);
		}
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

}
