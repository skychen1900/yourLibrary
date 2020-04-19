package sample.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.DragDropEvent;

import lombok.Getter;
import lombok.Setter;
import sample.common.constant.Constants;
import sample.entity.Movie;
import sample.entity.User;
import sample.logic.MovieManager;
import sample.util.interceptor.WithLog;
import sample.view.util.SessionInfo;
import sample.view.util.ViewUtil;

@Named
@ViewScoped
@WithLog
public class MovieCartView implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private List<Movie> moviesInCart;

	@Getter
	@Setter
	private List<Movie> moviesToBeLent;

	@Inject
	private MovieManager movieManager;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		moviesInCart = (List<Movie>) ViewUtil.getFromFlash(Constants.SEARCH_MOVIE_VIEW_MOVIES_IN_CART);
		moviesToBeLent = new ArrayList<Movie>();
	}

	//借りる映画にドロップ
	public void onDropToRent(DragDropEvent ddEvent) {
		Movie movie = ((Movie) ddEvent.getData());

		if (ddEvent.getDragId().startsWith("movieCartForm:cartGrid")
				&& "movieCartForm:cartField".equals(ddEvent.getDropId())) {
			return;
		}
		if (ddEvent.getDragId().startsWith("movieCartForm:rentGrid")
				&& "movieCartForm:rentField".equals(ddEvent.getDropId())) {
			return;
		}
		if (!moviesToBeLent.contains(movie)) {
			moviesToBeLent.add(movie);
			moviesInCart.remove(movie);
		}
	}

	//カート戻るにドロップ
	public void onDropToCart(DragDropEvent ddEvent) {
		Movie movie = ((Movie) ddEvent.getData());

		if (ddEvent.getDragId().startsWith("movieCartForm:cartGrid")
				&& "movieCartForm:cartField".equals(ddEvent.getDropId())) {
			return;
		}
		if (ddEvent.getDragId().startsWith("movieCartForm:rentGrid")
				&& "movieCartForm:rentField".equals(ddEvent.getDropId())) {
			return;
		}
		if (!moviesInCart.contains(movie)) {
			moviesInCart.add(movie);
			moviesToBeLent.remove(movie);
		}
	}

	//映画を借りる
	public String rentMovies() {
		SessionInfo sessionInfo = ViewUtil.getSessionInfo();
		User user = sessionInfo.getLoginUser();
		if (null == user) {
			ViewUtil.AddErrorMessage("ご利用中のユーザーがログインしていません", "");
			return null;
		}

		for (Movie movie : moviesToBeLent) {
			movieManager.lendMovie(movie, user);
		}
		return "searchMovie.xhtml?faces-redirect=true";
	}

	//前画面に戻る
	public String back() {
		return "searchMovie.xhtml?faces-redirect=true";
	}

}
