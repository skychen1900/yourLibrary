package sample.test;

import java.util.Locale;

import lombok.Getter;
import lombok.Setter;

public class ScreenDto {

	@Getter
	@Setter
	private String input;

	@Getter
	@Setter
	private String output;

	@Getter
	@Setter
	private String country;

	@Getter
	@Setter
	private String outputCountry;

	@Getter
	@Setter
	private Locale[] countries;

}
