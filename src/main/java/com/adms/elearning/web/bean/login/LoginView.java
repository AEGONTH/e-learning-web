package com.adms.elearning.web.bean.login;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import com.adms.common.entity.UserLogin;
import com.adms.elearning.entity.Student;
import com.adms.elearning.service.StudentService;
import com.adms.elearning.web.bean.base.BaseBean;
import com.adms.elearning.web.util.MessageUtils;
import com.adms.elearning.web.util.PropertyConfig;
import com.adms.utils.EncryptionUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@ManagedBean
@ViewScoped
public class LoginView extends BaseBean {

	private static final long serialVersionUID = -5671223478971772749L;

	private String loginId;

//	reg = ^(?=.*\d).{4,8}$
	private String password;

	private String firstName;
	private String lastName;

	private String focusOn;

	private boolean firstLogin = false;
	private boolean validateValueChange = false;

	@ManagedProperty(value="#{loginSession}")
	private LoginSession loginSession;

	@ManagedProperty(value="#{studentService}")
	private StudentService studentService;

	private final PropertyConfig cfg = PropertyConfig.getInstance();

	public LoginView() {

	}

	@PostConstruct
	public void initial() {
		initLoginValue();
		validateValueChange = false;
	}

	private void initLoginValue() {
		loginId = null;
		firstName = null;
		lastName = null;
		firstLogin = false;
		focusOn = "loginId";
	}

	public void toLoginPage() {

	}

	public String doLogin() {
//		System.out.println("doLogin()");
		boolean flag = false;
		try {
//			TODO Authentication Method
//			flag = callAuthenService();

			if(validateValueChange) {
				((UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmLogin:loginId")).setValid(false);
				MessageUtils.getInstance().addErrorMessage("loginMsg", getGlobalMsgValue("validate.err.citizen.id.change"));
				initLoginValue();
				return null;
			}

			if(StringUtils.isBlank(loginId)) {
				flag = false;
				((UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmLogin:loginId")).setValid(false);
				MessageUtils.getInstance().addErrorMessage("loginMsg", getGlobalMsgValue("validate.err.citizen.id.empty"));
			} else {
//				<!-- Check, is this loginId is first time -->
				firstLogin = !isLoginIDExisted(loginId);

				if(firstLogin) {
//					<!-- Validate firstName & lastName are blank? -->
					if(StringUtils.isNoneBlank(firstName) && StringUtils.isNoneBlank(lastName)) {
						//TODO Insert to DB
						Student example = new Student();
						example.setCitizenId(loginId);
						example.setFirstName(firstName.toUpperCase());
						example.setLastName(lastName.toUpperCase());

						studentService.add(example, SYSTEM_LOG_BY);
						flag = true;
					} else {
						if(StringUtils.isBlank(firstName)) {
							((UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmLogin:firstName")).setValid(false);
						}
						if(StringUtils.isBlank(lastName)) {
							((UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmLogin:lastName")).setValid(false);
						}
						MessageUtils.getInstance().addWarnMessage("loginMsg", "Enter your First name & Last name.");
						focusOn = "firstName";
						flag = false;
					}
				} else {
					flag = true;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

		if(flag) {
//			re-direct
//			<!-- put loginId to Session -->
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			ec.getSession(true);

			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("username", this.loginId);

			UserLogin u = new UserLogin(loginId);
			loginSession.setUserLogin(u);

			return "page/courseinfo?faces-redirect=true";
		} else {
			return null;
		}
	}

	private boolean isLoginIDExisted(String loginId) throws Exception {
		boolean flag = false;
		Student example = new Student();
		example.setCitizenId(loginId);
		List<Student> check = studentService.find(example);
		if(!check.isEmpty()) flag = true;
		return flag;
	}

	public void loginIdChangeListener(ValueChangeEvent e) {
//		System.out.println("loginIdChangeListener()");
		if(e.getOldValue() != null) {
			if(!e.getOldValue().toString().equals(e.getNewValue().toString())) {
				validateValueChange = true;
			} else {
				validateValueChange = false;
			}
		} else {
			validateValueChange = false;
		}
	}

//	public String doLogout() throws IOException {
//		if(delay < 1) {
//			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
//			ec.invalidateSession();
//			ec.redirect(ec.getRequestContextPath() + "/login.jsf");
//			return "loginPage";
//		} else {
//			delay--;
//			return null;
//		}
//	}

	@SuppressWarnings("unused")
	private boolean callAuthenService() throws Exception {
		String targetUrl = cfg.getValue("cfg.link.service.authen");
		String pathAuthen = cfg.getValue("cfg.link.service.authen.path.authen");

		String encrypted = EncryptionUtil.getInstance().encrypt(password);
//		System.out.println("encrypted: " + encrypted);
		UserLogin us = process(targetUrl, pathAuthen, new UserLogin(loginId, encrypted));

		if(us != null && us.getLoginSuccess().booleanValue() == true) {
			return true;
		}

		return false;
	}

	private UserLogin process(String target, String path, UserLogin userLogin) {
		Gson gson = new GsonBuilder().create();
		Response response = ClientBuilder.newClient()
				.target(target)
				.path(path)
				.request()
				.header("strJson", gson.toJson(userLogin))
				.get();
		String resStr = response.readEntity(String.class);
//		System.out.println("status: " + response.getStatus());

		gson = new GsonBuilder().create();

		UserLogin us = gson.fromJson(resStr, UserLogin.class);
		return us;
	}

	public String getLoginId() {
		return loginId;
	}


	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public boolean isFirstLogin() {
		return firstLogin;
	}

	public void setFirstLogin(boolean firstLogin) {
		this.firstLogin = firstLogin;
	}

	public LoginSession getLoginSession() {
		return loginSession;
	}

	public void setLoginSession(LoginSession loginSession) {
		this.loginSession = loginSession;
	}

	public void setStudentService(StudentService studentService) {
		this.studentService = studentService;
	}

	public String getFocusOn() {
		return focusOn;
	}

	public void setFocusOn(String focusOn) {
		this.focusOn = focusOn;
	}

	public boolean isValidateValueChange() {
		return validateValueChange;
	}

	public void setValidateValueChange(boolean validateValueChange) {
		this.validateValueChange = validateValueChange;
	}
}
