package com.mycompany.messages;

import java.io.IOException;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

/**
 * This class performs authentication in the Project Billboard
 * application.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class AuthenticateAction {
    private String username;
    private String password;
    private boolean remember;
    private boolean rememberSet;
    private EmployeeRegistryBean empReg;
    private String origURL;

    /**
     * Returns the current username, or the value of a "username"
     * cookie if no username is set.
     */
    public String getUsername() {
	if (username == null) {
	    FacesContext context = FacesContext.getCurrentInstance();
	    Map cookies = context.getExternalContext().getRequestCookieMap();
	    Cookie c = (Cookie) cookies.get("username");
	    if (c != null) {
		username = c.getValue();
	    }
	}
	return username;
    }

    /**
     * Sets the username.
     */
    public void setUsername(String username) {
	this.username = username;
    }

    /**
     * Returns the current password, or the value of a "password"
     * cookie if no username is set.
     */
    public String getPassword() {
	if (password == null) {
	    FacesContext context = FacesContext.getCurrentInstance();
	    Map cookies = context.getExternalContext().getRequestCookieMap();
	    Cookie c = (Cookie) cookies.get("password");
	    if (c != null) {
		password = c.getValue();
	    }
	}
	return password;
    }

    /**
     * Sets the password.
     */
    public void setPassword(String password) {
	this.password = password;
    }

    /**
     * Returns the "remember" property value, or "true" if this property
     * isn't set and a "password" cookie has a value.
     */
    public boolean getRemember() {
	if (!rememberSet) {
	    FacesContext context = FacesContext.getCurrentInstance();
	    Map cookies = context.getExternalContext().getRequestCookieMap();
	    Cookie c = (Cookie) cookies.get("password");
	    if (c != null) {
		remember = true;
	    }
	}
	return remember;
    }

    /**
     * Sets the "remember" property value.
     */
    public void setRemember(boolean remember) {
	this.remember = remember;
	rememberSet = true;
    }

    /**
     * Sets the registry holding user information.
     */
    public void setRegistry(EmployeeRegistryBean empReg) {
	this.empReg = empReg;
    }

    /**
     * Returns the originally requested URL, or null if none is set.
     */
    public String getOrigURL() {
	// Need to do this because the access control filter uses a
	// different parameter name than the login form
	if (origURL == null) {
	    FacesContext context = FacesContext.getCurrentInstance();
	    Map param = 
		context.getExternalContext().getRequestParameterMap();
	    String s = (String) param.get("origURL");
	    if (s != null) {
		origURL = s;
	    }
	}
	return origURL;
    }

    /**
     * Sets the originally requested URL.
     */
    public void setOrigURL(String origURL) {
	this.origURL = origURL;
    }

    /**
     * Autheticates a user with help from the EmployeeRegistryBean.
     * If the user can be authenticated, the "validUser" session 
     * attribute is set to an instance of the EmployeeBean, to
     * serve as an authentication token in this application.
     * <p>
     * Cookies with the user name and password are set or reset
     * as specified by the "remember" request parameter.
     *
     */
    public String authenticate() {

	String result = null;
	FacesContext context = FacesContext.getCurrentInstance();

	EmployeeBean emp = empReg.authenticate(username, password);
	if (emp != null) {
	    Map sessionMap = 
		context.getExternalContext().getSessionMap();
	    sessionMap.put("validUser", emp);
	    setLoginCookies(context, remember, username, password);
                
	    // Next page is the originally requested URL or main
	    if (origURL != null && origURL.length() != 0) {
		String newPath = context.getApplication().getViewHandler().
		    getActionURL(context, origURL);
		try {
		    context.getExternalContext().redirect(newPath);
		}
		catch (IOException e) {}
		context.responseComplete();
		result = "newViewSet";
	    }
	    else {
		result = "success";
	    }
	}
	else {
	    // Invalid login.
	    FacesMessage msg = 
		new FacesMessage(FacesMessage.SEVERITY_ERROR,
				 "Invalid username or password", null);
	    context.addMessage(null, msg);
	    result = "failure";
	}
	return result;
    }

    /**
     * Set or "delete" the login cookies, depending on the value of the
     * "remember" parameter.
     */
    private void setLoginCookies(FacesContext context, boolean remember,
				 String username, String password) {

	HttpServletRequest request = 
	    (HttpServletRequest) context.getExternalContext().getRequest();
	HttpServletResponse response = 
	    (HttpServletResponse) context.getExternalContext().getResponse();
	Cookie usernameCookie = new Cookie("username", username);
	Cookie passwordCookie = new Cookie("password", password);
	// Cookie age in seconds: 30 days * 24 hours * 60 minutes * 60 seconds
	int maxAge = 30 * 24 * 60 * 60;
	if (!remember) {
	    // maxAge = 0 to delete the cookie
	    maxAge = 0;
	}
	usernameCookie.setMaxAge(maxAge);
	passwordCookie.setMaxAge(maxAge);
	usernameCookie.setPath(request.getContextPath());
	passwordCookie.setPath(request.getContextPath());
	response.addCookie(usernameCookie);
	response.addCookie(passwordCookie);
    }
}
