package sample.view.util;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;
import sample.entity.User;
import sample.util.interceptor.WithLog;

@Named(value = "sessionInfo")
@SessionScoped
@WithLog
public class SessionInfo implements Serializable {
	private static final long serialVersionUID = 9186759612086888662L;

	@Getter
	@Setter
	private User loginUser;

	public String logout() {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		externalContext.invalidateSession();
		HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
		try {
			request.logout();
		} catch (ServletException e) {
			e.printStackTrace();
		}
		return "/index.xhtml?faces-redirect=true";
	}

	public String getTheme() {
		if (loginUser == null || loginUser.getTheme().length() == 0) {
			return "afternoon";
		}
		return loginUser.getTheme();
	}

}