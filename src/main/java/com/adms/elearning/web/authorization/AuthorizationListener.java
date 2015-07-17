package com.adms.elearning.web.authorization;

import java.io.IOException;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpSession;

public class AuthorizationListener implements PhaseListener {

	private static final long serialVersionUID = -1177538587408767802L;

	@Override
	public void afterPhase(PhaseEvent event) {
		FacesContext facesContext = event.getFacesContext();
		String currentPage = facesContext.getViewRoot().getViewId();

//		System.out.println("Current Page: " + currentPage);
		boolean isExceptionalPage = ((currentPage.lastIndexOf("importdata.xhtml")) > -1
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
				redirectToLoginPage(facesContext);
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			Object currentUser = session.getAttribute("username");
//			System.out.println("Object currentUser: " + currentUser);
			if(!isLoginPage && (currentUser == null || currentUser == "")) {
				try {
					redirectToLoginPage(facesContext);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void redirectToLoginPage(FacesContext facesContext) throws IOException {
		NavigationHandler navHandler = facesContext.getApplication().getNavigationHandler();
		navHandler.handleNavigation(facesContext, null, "loginPage");
	}

	@Override
	public void beforePhase(PhaseEvent event) {

	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

}
