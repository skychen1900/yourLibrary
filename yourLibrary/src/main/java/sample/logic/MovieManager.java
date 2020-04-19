package sample.logic;

import java.util.List;

import sample.entity.LendHistory;
import sample.entity.Movie;
import sample.entity.User;

public interface MovieManager {

	Movie createMovie(String string);

	Movie findById(long id);

	List<LendHistory> findAllLendHistory();

	LendHistory lendMovie(Movie movie, User user);

	LendHistory returnMovie(LendHistory history2);

	List<Movie> findAll();

	Movie updateMovie(Movie find1);

	Movie findByTitle(String string);

	boolean removeMovie(Movie find1);

	LendHistory updateLendHistory(LendHistory history1);

	LendHistory findLendHistoryById(long id);

	boolean removeLendHistory(LendHistory history1);

	List<String> getEnteredTitles();

	List<String> getEnteredCategory();

	List<String> getEnteredOutline();

	List<Movie> searchMovie(String title, String category, String outline, Boolean isLent);

	List<LendHistory> findAllLendHistoryByUser(Long lendUserId);

}
