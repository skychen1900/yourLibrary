package sample.view.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import sample.entity.Movie;

public class SearchMoiveScreenDto {

    @Getter
    @Setter
    private String title; //タイトル

    @Getter
    @Setter
    private String category; //カタログ

    @Getter
    @Setter
    private String outline; //あらすじ

    @Getter
    @Setter
    private List<Movie> selectedMovies;

    @Getter
    @Setter
    private IdEntityListDataModel<Movie> movieModel;

    @Getter
    @Setter
    private List<Movie> moviesInCart;

    private boolean isSelected;

    @Getter
    @Setter
    private List<String> enteredTitles;

    @Getter
    @Setter
    private List<String> enteredCategory;

    @Getter
    @Setter
    private List<String> enteredOutline;

    private List<Movie> movies;

    public boolean getIsSelected() {
        return isSelected;
    }

    public boolean setIsSelected(boolean _isSelected) {
        return this.isSelected = _isSelected;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

}
