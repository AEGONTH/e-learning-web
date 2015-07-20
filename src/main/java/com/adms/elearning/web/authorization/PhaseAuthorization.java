package com.adms.elearning.web.authorization;

import java.io.IOException;

import javax.faces.application.NavigationHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseListener;

public abstract class PhaseAuthorization implements PhaseListener {

	private static final long serialVersionUID = 3774925579205518826L;

	public PhaseAuthorization() {

	}

	protected void redirectToLogin() throws IOException {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		ec.invalidateSession();
		ec.redirect(ec.getRequestContextPath() + "/login.jsf");
	}

	protected void redirectToLogin(FacesContext facesContext)	{
		NavigationHandler nh = facesContext.getApplication().getNavigationHandler();
		nh.handleNavigation(facesContext, null, "loginPage");
	}

}
