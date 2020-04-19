package sample.view.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import sample.entity.Movie;

//未使用
public class MovewCardViewScreenDto {

	@Getter
	@Setter
	private List<Movie> moviesInCart;

	@Getter
	@Setter
	private List<Movie> moviesToBeLent;

}
