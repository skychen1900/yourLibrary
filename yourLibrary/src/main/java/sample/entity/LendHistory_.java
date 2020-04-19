package sample.entity;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2018-07-22T17:13:49.056+0900")
@StaticMetamodel(LendHistory.class)
public class LendHistory_ {
	public static volatile SingularAttribute<LendHistory, Long> id;
	public static volatile SingularAttribute<LendHistory, Date> lendDate;
	public static volatile SingularAttribute<LendHistory, Date> dueDate;
	public static volatile SingularAttribute<LendHistory, Date> returnDate;
	public static volatile SingularAttribute<LendHistory, String> review;
	public static volatile SingularAttribute<LendHistory, Double> starRating;
	public static volatile SingularAttribute<LendHistory, Movie> movie;
	public static volatile SingularAttribute<LendHistory, User> lendUser;
}
