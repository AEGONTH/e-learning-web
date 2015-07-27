package com.adms.elearning.web.bean.admin;

import javax.annotation.PostConstruct;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;

import com.adms.elearning.web.bean.base.BaseBean;
import com.adms.elearning.web.util.MessageUtils;
import com.adms.utils.DateUtil;

@ManagedBean
@ViewScoped
public class AdminView extends BaseBean {

	private static final long serialVersionUID = 7661072024938298885L;

	@ManagedProperty(value="#{adminSession}")
	private AdminSession admSession;

	private final String specificToken = "welcome123";

	private boolean isVisible;
	private String token;

	@PostConstruct
	private void init() {
		isVisible = validateToken(admSession.getToken());

		RequestContext context = RequestContext.getCurrentInstance();
		if(isVisible) {

		} else {
			context.execute("PF('dlgLogin').show();");
		}
	}

	public void signIn(ActionEvent event) {
		RequestContext context = RequestContext.getCurrentInstance();
		boolean flag;

		if(validateToken(this.token)) {
			admSession.setToken(this.token);
			admSession.setSignInDate(DateUtil.getCurrentDate());
			flag = true;
		} else {
			flag = false;
			((UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmLoginAdmin:token")).setValid(false);
			MessageUtils.getInstance().addErrorMessage("frmLoginAdmin:validAdmMsg", "Invalid Secret Key!");
		}

		context.addCallbackParam("loggedIn", flag);
		if(flag) {
			context.execute("PF('dlgLogin').hide();");
		} else {
			context.execute("PF('dlgLogin').jq.effect('shake', {times:5}, 100);");
		}
	}

	public void signOut() {
		admSession.clearState();
		FacesContext fc = FacesContext.getCurrentInstance();
		NavigationHandler nh = fc.getApplication().getNavigationHandler();
		nh.handleNavigation(fc, null, "/admin.jsf?faces-redirect=true");
	}

	private boolean validateToken(String token) {
		if(!StringUtils.isBlank(token) && token.equals(specificToken)) {
			return true;
		}
		return false;
	}

	/* Getter & Setter */
	public AdminSession getAdmSession() {
		return admSession;
	}

	public void setAdmSession(AdminSession admSession) {
		this.admSession = admSession;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
