package sample.view;

import java.io.Serializable;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import sample.entity.User;
import sample.logic.UserManager;
import sample.util.interceptor.WithLog;
import sample.view.util.SessionInfo;
import sample.view.util.ViewUtil;

@Named
@ViewScoped
@WithLog
/*
 * Edit User BackingBean Class
 */
public class ChangeThemeView implements Serializable {
	@Getter
	@Setter
	private String selectedTheme;

	@Getter
	@Setter
	private TreeMap<String, String> themes;

	@Inject
	private UserManager userManager;

	@PostConstruct
	public void init() {
		themes = new TreeMap<String, String>();
		themes.put("Afterdark", "afterdark");
		themes.put("Afternoon", "afternoon");
		themes.put("Afterwork", "afterwork");
		themes.put("Aristo", "aristo");
		themes.put("Black-Tie", "black-tie");
		themes.put("Blitzer", "blitzer");
		themes.put("Bluesky", "bluesky");
		themes.put("Bootstrap", "bootstrap");
		themes.put("Casablanca", "casablanca");
		themes.put("Cupertino", "cupertino");
		themes.put("Cruze", "cruze");
		themes.put("Dark-Hive", "dark-hive");
		themes.put("Delta", "delta");
		themes.put("Dot-Luv", "dot-luv");
		themes.put("Eggplant", "eggplant");
		themes.put("Excite-Bike", "excite-bike");
		themes.put("Flick", "flick");
		themes.put("Glass-X", "glass-x");
		themes.put("Home", "home");
		themes.put("Hot-Sneaks", "hot-sneaks");
		themes.put("Humanity", "humanity");
		themes.put("Le-Frog", "le-frog");
		themes.put("Midnight", "midnight");
		themes.put("Mint-Choc", "mint-choc");
		themes.put("Overcast", "overcast");
		themes.put("Pepper-Grinder", "pepper-grinder");
		themes.put("Redmond", "redmond");
		themes.put("Rocket", "rocket");
		themes.put("Sam", "sam");
		themes.put("Smoothness", "smoothness");
		themes.put("South-Street", "south-street");
		themes.put("Start", "start");
		themes.put("Sunny", "sunny");
		themes.put("Swanky-Purse", "swanky-purse");
		themes.put("Trontastic", "trontastic");
		themes.put("UI-Darkness", "ui-darkness");
		themes.put("UI-Lightness", "ui-lightness");
		themes.put("Vader", "vader");

		selectedTheme = "";
		User user = ViewUtil.getSessionInfo().getLoginUser();
		if (user != null) {
			selectedTheme = user.getTheme();
		}
	}

	public String saveTheme() {
		SessionInfo sessionInfo = ViewUtil.getSessionInfo();
		User loginUser = sessionInfo.getLoginUser();
		loginUser.setTheme(selectedTheme);
		userManager.updateUser(loginUser);
		ViewUtil.AddMessage("テーマ変更", "選択されたテーマを適用しました。");
		return null;
	}
}