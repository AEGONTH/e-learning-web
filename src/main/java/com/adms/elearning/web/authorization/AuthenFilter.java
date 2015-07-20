package com.adms.elearning.web.authorization;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(filterName="AuthenFilter", urlPatterns="/page/*")
public class AuthenFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		boolean redirectToLogin = false;

		HttpServletRequest reqt = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		HttpSession session = reqt.getSession(false);

		String reqURI = reqt.getRequestURI();
//		System.out.println(reqURI);
		if(session == null) {
			redirectToLogin = true;
		} else {
			Object username = session.getAttribute("username");
//			System.out.println("username: " + username);
			if(redirectToLogin == false
					&& (username == null
						|| username.equals(""))) {
				redirectToLogin = true;
			}

			if(redirectToLogin == false
					&& username != null
					&& (reqURI.contains("page/question") || reqURI.contains("page/final"))
					&& session.getAttribute("courseEnrolmentId") == null) {
				redirectToLogin = true;
			}
		}

		if(redirectToLogin) {
			String contextPath = reqt.getContextPath();
			resp.sendRedirect(contextPath + "/login.jsf");
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

//	private void redirectToLogin() throws IOException {
//		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
//		ec.invalidateSession();
//		ec.redirect(ec.getRequestContextPath() + "/login.jsf");
//	}

}
