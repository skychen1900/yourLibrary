package sample.view;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.DragDropEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import sample.common.constant.Constants;
import sample.entity.Movie;
import sample.entity.User;
import sample.logic.MovieManager;
import sample.util.IgnoreLogging;
import sample.util.interceptor.WithLog;
import sample.view.util.SessionInfo;
import sample.view.util.ViewUtil;

@Named
//@SessionScoped
@ViewScoped
@WithLog
public class MovieCartView implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Movie> moviesInCart;

    private List<Movie> moviesToBeLent;

    private Map<String, byte[]> contents;

    @Inject
    private MovieManager movieManager;

    @PostConstruct
    public void init() {

        //Flashからカートに入れたもの
        moviesInCart = (List<Movie>) ViewUtil.getFromFlash(Constants.SEARCH_MOVIE_VIEW_MOVIES_IN_CART);
        moviesToBeLent = new ArrayList<Movie>();
        contents = new HashMap<>();

        if (moviesInCart != null && moviesInCart.size() > 0) {
            for (Movie m : moviesInCart) {
                contents.put(Long.toString(m.getId()), m.getImageData());

            }
        }
    }

    //借りる映画にドロップ
    @IgnoreLogging
    public void onDropToRent(DragDropEvent<?> ddEvent) {
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
    @IgnoreLogging
    public void onDropToCart(DragDropEvent<?> ddEvent) {
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

    public StreamedContent getImage() {

        FacesContext context = FacesContext.getCurrentInstance();

        System.out.println("PhaseId: " + context.getCurrentPhaseId());

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the view. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        } else {
            // So, browser is requesting the image. Get ID value from actual request param.
            String id = context.getExternalContext().getRequestParameterMap().get("id");
            System.out.println("image_id: " + id);

            if (null == id) {
                return null;
            } else {
                return DefaultStreamedContent.builder()
                        .contentType("image/png")
                        .stream(() -> {
                            try {
                                return new ByteArrayInputStream(contents.get(id));
                            } catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                        })
                        .build();

                //            return new DefaultStreamedContent(new ByteArrayInputStream(contents.get(id)));
            }
        }

    }

    @IgnoreLogging
    public List<Movie> getMoviesInCart() {
        return moviesInCart;
    }

    @IgnoreLogging
    public void setMoviesInCart(List<Movie> moviesInCart) {
        this.moviesInCart = moviesInCart;
    }

    @IgnoreLogging
    public List<Movie> getMoviesToBeLent() {
        return moviesToBeLent;
    }

    @IgnoreLogging
    public void setMoviesToBeLent(List<Movie> moviesToBeLent) {
        this.moviesToBeLent = moviesToBeLent;
    }

    //    @IgnoreLogging
    //    public Map<String, DefaultStreamedContent> getContents() {
    //        return contents;
    //    }

    //    @IgnoreLogging
    //    public void setContents(Map<String, DefaultStreamedContent> contents) {
    //        this.contents = contents;
    //    }

}
