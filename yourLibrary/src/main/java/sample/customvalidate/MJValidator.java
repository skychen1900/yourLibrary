package sample.customvalidate;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author chenhongming
 *
 */
public class MJValidator implements ConstraintValidator<MJ, String> {

	private final static List<String> songs = new ArrayList<String>() {
		{
			add("Remember The Time");
			add("Stranger In Moscow");
			add("You Are Not Alone");
		}
	};

	@Override
	public void initialize(MJ constraintAnnotation) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		// TODO 自動生成されたメソッド・スタブ
		return songs.stream().anyMatch(song -> song.equals(value));
	}

}
