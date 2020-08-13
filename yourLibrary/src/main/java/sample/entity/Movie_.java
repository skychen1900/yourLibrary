package sample.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "Dali", date = "2018-07-22T17:12:19.671+0900")
@StaticMetamodel(Movie.class)
public class Movie_ {
    public static volatile SingularAttribute<Movie, Long> id;
    public static volatile SingularAttribute<Movie, String> title;
    public static volatile SingularAttribute<Movie, String> outline;
    public static volatile SingularAttribute<Movie, String> category;
    public static volatile SingularAttribute<Movie, Boolean> isLent;
    public static volatile SingularAttribute<Movie, String> image;
    public static volatile SingularAttribute<Movie, byte[]> imageData;
    public static volatile ListAttribute<Movie, LendHistory> lendHistories;
}
