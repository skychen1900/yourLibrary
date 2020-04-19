package sample.view;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.event.ActionEvent;
import javax.faces.lifecycle.ClientWindow;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import sample.entity.User;
import sample.logic.UserManagerImpl;
import sample.util.interceptor.WithLog;
import sample.view.util.SessionInfo;
import sample.view.util.ViewUtil;

@Named
@ViewScoped
@WithLog
/*
 * Login BackingBean Class
 */
public class LoginView implements Serializable {

	private static final long serialVersionUID = 1L;
	//private static final Logger logger = LoggerFactory.getLogger(LoginView.class);

	@Getter
	@Setter
	private String account;
	@Getter
	@Setter
	private String password;

	@Getter
	@Setter
	private String oldPassword;
	@Getter
	@Setter
	private String newPassword;
	@Getter
	@Setter
	private String newPasswordConfirm;

	@Getter
	@Setter
	private boolean testA;

	@Inject
	private UserManagerImpl userManager;

	@PostConstruct
	public void init() {
		System.out.println("[View Scope] post construct : " + hashCode());
	}

	@PreDestroy
	public void preDestroy() {
		System.out.println("[View Scope] pre construct : " + hashCode());
	}

	public void loginListener(ActionEvent ae) {
		System.out.println("Call");
	}

	public String login() {
		User loginUser = userManager.login(account, password);
		if (loginUser == null) {
			ViewUtil.AddErrorMessage("ログイン失敗", "アカウントまたはパスワードが一致しません。");
			return null;
		}

		//Flash Test
		ClientWindow cw = FacesContext.getCurrentInstance().getExternalContext().getClientWindow();
		if (null != cw) {
			System.out.println("ClientWindow:" + cw.getId());
		} else {
			System.out.println("ClientWindow is disabled!");
		}

		Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
		flash.put("test1", "value1");

		//		System.out.println("flash1:");
		for (String s : flash.keySet()) {
			System.out.println(s + "=" + flash.get(s));
		}

		//		System.out.println("requestMap1:");
		Map<String, Object> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
		for (String s : requestMap.keySet()) {
			System.out.println(s + "=" + requestMap.get(s));
		}

		//		System.out.println("sessiontMap1:");
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		for (String s : sessionMap.keySet()) {
			//			System.out.println(s + "=" + sessionMap.get(s));
		}

		SessionInfo sessionInfo = ViewUtil.getSessionInfo();
		sessionInfo.setLoginUser(loginUser);
		return "editUser.xhtml?faces-redirect=true";
	}

	//パスワード変更
	public String changePassword() {

		SessionInfo sessionInfo = ViewUtil.getSessionInfo();
		User loginUser = sessionInfo.getLoginUser();

		if (!oldPassword.equals(loginUser.getPassword())) {
			ViewUtil.AddErrorMessage("パスワード変更失敗", "入力したパスワードと既存のパスワードが一致しません。");
			return null;
		}

		if (!newPassword.equals(newPasswordConfirm) & null != newPassword) {
			ViewUtil.AddErrorMessage("パスワード変更失敗", "２回入力したパスワードが一致しません。");
			return null;
		}

		loginUser.setPassword(newPassword);
		userManager.updateUser(loginUser);
		ViewUtil.AddMessage("パスワードが変更しました", null);

		return null;
	}
}