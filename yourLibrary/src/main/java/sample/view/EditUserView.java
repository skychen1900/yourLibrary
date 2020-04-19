package sample.view;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;

import lombok.Getter;
import lombok.Setter;
import sample.entity.User;
import sample.logic.UserManager;
import sample.util.interceptor.WithLog;
import sample.view.dto.IdEntityListDataModel;
import sample.view.dto.UserScreenDto;
import sample.view.util.ViewUtil;

@Named
@ViewScoped
@WithLog
/*
 * Edit User BackingBean Class
 */
public class EditUserView implements Serializable {

	private static final long serialVersionUID = -7001108729176075057L;

	@Getter
	@Setter
	private UserScreenDto newUserDto = new UserScreenDto();//新規ユーザー情報定義DTO

	@Getter
	@Setter
	private IdEntityListDataModel<User> usersListDto;// ユーザー表示一覧の全てユーザーの

	@Getter
	@Setter
	private List<User> selectedUsers;//選択分ユーザー

	private boolean isSelected;

	@Getter
	@Setter
	private String newPassword;

	@Inject
	private UserManager userManager;

	@PostConstruct
	public void init() {
		List<User> users = userManager.findAll();
		usersListDto = new IdEntityListDataModel<User>(users);
	}

	public void addUser() {

		if (newUserDto.getAccount() == null || newUserDto.getName() == null) {
			ViewUtil.AddWarningMessage("ユーザの追加", "アカウントと名前を入力してください。");
			return;
		}
		User user = userManager.createUser(newUserDto.getAccount(), newUserDto.getName());
		user.setPassword(newUserDto.getPassword());
		user.setEmail(newUserDto.getEmail());
		user.setIsAdmin(newUserDto.isAdmin());
		user = userManager.updateUser(user);
		List<User> users = userManager.findAll();
		usersListDto.setWrappedData(users);
		ViewUtil.AddMessage("ユーザの追加", "ユーザーを追加しました。");
		return;
	}

	public String removeUser() {
		if (selectedUsers == null || selectedUsers.isEmpty()) {
			return null;
		}

		userManager.removeUser(selectedUsers);
		List<User> users = userManager.findAll();
		usersListDto.setWrappedData(users);
		ViewUtil.AddMessage("ユーザの削除", selectedUsers.size() + "件のユーザを削除しました。");
		isSelected = false;//削除後はボタンを無効にする。
		return null;
	}

	public String updatePassword() {
		if (selectedUsers == null || selectedUsers.isEmpty()) {
			return null;
		}
		User user = selectedUsers.get(0);
		if (newPassword.equals(user.getPassword())) {
			ViewUtil.AddWarningMessage("パスワードの変更", "変更前後のパスワードが同じのため、パスワードの変更が行いません");
			return null;//同じなら更新しない。
		}
		user.setPassword(newPassword);
		userManager.updateUser(user);
		ViewUtil.AddMessage("パスワードの変更", user.getName() + "のパスワードを変更しました。");
		return null;
	}

	public void onRowEdit(RowEditEvent event) {
		User user = (User) event.getObject();
		userManager.updateUser(user);
		ViewUtil.AddMessage("ユーザの編集", "ユーザ " + user.getName() + " を更新しました。");
	}

	public void onRowEditCancel(RowEditEvent event) {
		ViewUtil.AddMessage("ユーザの編集", "編集をキャンセルしました。");
	}

	public boolean getIsSelected() {
		return isSelected;
	}

	public void handleSelect(SelectEvent event) {
		isSelected = (getSelectedUsers().size() > 0);
	}

	public void handleUnselect(UnselectEvent event) {
		isSelected = (getSelectedUsers().size() > 0);
	}

	public void handleToggleSelect(ToggleSelectEvent event) {
		isSelected = (getSelectedUsers().size() > 0);
	}

}