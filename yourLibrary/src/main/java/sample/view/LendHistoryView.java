package sample.view;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import sample.entity.LendHistory;
import sample.entity.Movie;
import sample.entity.User;
import sample.logic.MovieManager;
import sample.util.interceptor.WithLog;
import sample.view.dto.IdEntityListDataModel;
import sample.view.util.SessionInfo;
import sample.view.util.ViewUtil;

@Named
@ViewScoped
@WithLog
public class LendHistoryView implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private IdEntityListDataModel<LendHistory> lendHistoryModel;

	@Getter
	@Setter
	private List<LendHistory> lendHistories;

	@Getter
	@Setter
	private List<LendHistory> selectedLendHistory;

	@Inject
	private MovieManager movieManager;

	@Inject
	private SessionInfo sessionInfo;

	@PostConstruct
	public void init() {

		User currentUser = sessionInfo.getLoginUser();
		if (null == currentUser) {
			//Todo ユーザーが未ログインのため、最初のログイン画面を表示させる
			return;
		}

		// 貸出中の映画を表示する
		lendHistories = movieManager.findAllLendHistoryByUser(currentUser.getId());
		lendHistoryModel = new IdEntityListDataModel<>(lendHistories);
	}

	public void returnLend() {
		//チェック
		if (null == selectedLendHistory || selectedLendHistory.size() == 0) {
			ViewUtil.AddErrorMessage("注意", "返す映画が選択されていません");
			return;
		}

		for (LendHistory lendHistory : selectedLendHistory) {
			lendHistory.setReturnDate(Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
			movieManager.updateLendHistory(lendHistory);
			Movie movie = lendHistory.getMovie();
			movie.setIsLent(false);
			movieManager.updateMovie(movie);
		}
		return;
	}

	//前画面に戻る
	public String back() {
		//Todo
		return "";
	}

}
