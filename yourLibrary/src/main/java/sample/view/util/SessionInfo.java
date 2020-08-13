package sample.view.util;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import sample.entity.User;
import sample.util.IgnoreLogging;
import sample.util.interceptor.WithLog;

@Named(value = "sessionInfo")
@SessionScoped
@WithLog
public class SessionInfo implements Serializable {
    private static final long serialVersionUID = 9186759612086888662L;

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

    @IgnoreLogging
    public String getTheme() {
        if (loginUser == null || loginUser.getTheme().length() == 0) {
            return "afternoon";
        }
        return loginUser.getTheme();
    }

    @IgnoreLogging
    public User getLoginUser() {
        return loginUser;
    }

    @IgnoreLogging
    public void setLoginUser(User loginUser) {
        this.loginUser = loginUser;
    }

}