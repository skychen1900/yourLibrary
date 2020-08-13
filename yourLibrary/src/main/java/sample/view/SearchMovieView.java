package sample.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;
import sample.common.constant.Constants;
import sample.entity.Movie;
import sample.logic.MovieManagerImpl;
import sample.util.interceptor.WithLog;
import sample.view.dto.IdEntityListDataModel;
import sample.view.dto.SearchMoiveScreenDto;
import sample.view.util.ViewUtil;

@Named
@ViewScoped
@WithLog
/*
 * Search Movie BackingBean Class
 */
public class SearchMovieView implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(LoginView.class);

    @Getter
    @Setter
    private SearchMoiveScreenDto searchMovieScreenDto;

    @Inject
    private MovieManagerImpl movieManager;

    @PostConstruct
    //初期表示
    public void init() {
        searchMovieScreenDto = new SearchMoiveScreenDto();

        ArrayList<Movie> movies = new ArrayList<Movie>();
        searchMovieScreenDto.setMovieModel(new IdEntityListDataModel<Movie>(movies));
        searchMovieScreenDto.setEnteredTitles(movieManager.getEnteredTitles());
        searchMovieScreenDto.setEnteredCategory(movieManager.getEnteredCategory());
        searchMovieScreenDto.setEnteredOutline(movieManager.getEnteredOutline());
    }

    //映画の検索
    public void searchMovie(ActionEvent actionEvent) {
        //検索オプション：　null→貸出中も含める
        List<Movie> movies = movieManager.searchMovie(searchMovieScreenDto.getTitle(),
                searchMovieScreenDto.getCategory(), searchMovieScreenDto.getOutline(), null);
        logger.debug("Movies:", movies);

        searchMovieScreenDto.setMovieModel(new IdEntityListDataModel<Movie>(movies));//画面表示用
        searchMovieScreenDto.setMovies(movies);//在庫中の映画

    }

    //カートに追加。10件までを制限としている。
    public String addCart() {

        //選択されたものをチェック
        if (!searchMovieScreenDto.getIsSelected()) {
            ViewUtil.AddWarningMessage("注意", "カードに追加したい映画が選択されていないため、ご確認ください");
            return "";
        }

        List<Movie> moviesInCart = searchMovieScreenDto.getMoviesInCart(); //カートの映画
        List<Movie> selectedMovie = searchMovieScreenDto.getSelectedMovies(); //選択中の映画
        List<Movie> movies = searchMovieScreenDto.getMovies(); //在庫中の映画

        if (moviesInCart == null) {
            moviesInCart = new ArrayList<>();
        }

        if (moviesInCart.size() >= 10) {
            ViewUtil.AddErrorMessage("制限", "カートに入れられるのは10件までです。");
            return null;
        }

        for (Movie m : selectedMovie) {
            if (!moviesInCart.contains(m)) {
                moviesInCart.add(m);
                movies.remove(m);

                //画面DTO設定
                searchMovieScreenDto.getMovieModel().setWrappedData(movies);
                searchMovieScreenDto.setMoviesInCart(moviesInCart);
                searchMovieScreenDto.setSelectedMovies(null);
                searchMovieScreenDto.setIsSelected(false);

                ViewUtil.AddMessage("成功", "選択中の" + m.getTitle() + "映画をカートに入れました。");
            }
        }

        return null;
    }

    //viewCart.xhtmlへの遷移。Flashにカートの中の映画を設定する。
    public String viewCart() {
        List<Movie> moviesInCart = searchMovieScreenDto.getMoviesInCart(); //カートの映画
        if (moviesInCart == null || moviesInCart.size() == 0) {
            ViewUtil.AddErrorMessage("エラー", "カートが空です。");
            return null;
        }
        ViewUtil.putToFlash(Constants.SEARCH_MOVIE_VIEW_MOVIES_IN_CART, moviesInCart);
        return "/viewCart.xhtml?faces-redirect=true";
    }

    //検索欄補足
    public List<String> completeTitle(String input) {
        return searchMovieScreenDto.getEnteredTitles()
                .stream()
                .filter(e -> e.contains(input))
                .collect(Collectors.toList());
    }

    public List<String> completeCategory(String input) {
        return searchMovieScreenDto.getEnteredCategory()
                .stream()
                .filter(e -> e.contains(input))
                .collect(Collectors.toList());
    }

    public List<String> completeOutline(String input) {
        return searchMovieScreenDto.getEnteredOutline()
                .stream()
                .filter(e -> e.contains(input))
                .collect(Collectors.toList());
    }

    // チェックボクスが選択されたら、「カート追加」リンクを活性化
    public void onRowSelect(SelectEvent event) {
        this.searchMovieScreenDto.setIsSelected(true);
    }

    // チェックボクスの選択がなくなったら、「カート追加」リンクを非活性
    public void onRowUnSelect(SelectEvent event) {
        this.searchMovieScreenDto.setIsSelected(false);
    }

}
