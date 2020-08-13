package sample.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "LEND_HISTORY")
//貸出履歴
public class LendHistory implements IdEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;

    //借りた日
    @Temporal(TemporalType.DATE)
    @Column(name = "LEND_DATE")
    private Date lendDate;

    //返却締切日。超えても特にペナルティはないです。
    @Temporal(TemporalType.DATE)
    @Column(name = "DUE_DATE")
    private Date dueDate;

    //返却日
    @Temporal(TemporalType.DATE)
    @Column(name = "RETURN_DATE")
    private Date returnDate;

    //レビュー（感想）
    @Column(name = "REVIEW")
    private String review;

    //５つ星までの評価。GUIがあります。それを使いたいための属性です。
    @Column(name = "STAR_RATING")
    private double starRating;

    //借りられた『映画』への参照
    @ManyToOne
    @JoinColumn(name = "MOVIE_ID", referencedColumnName = "ID")
    @JsonBackReference
    private Movie movie;

    //借りた『ユーザ』への参照
    @ManyToOne
    @JoinColumn(name = "LENDUSER_ID", referencedColumnName = "ID")
    @JsonBackReference
    private User lendUser;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public Date getLendDate() {
        return lendDate;
    }

    public void setLendDate(Date lendDate) {
        this.lendDate = lendDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public double getStarRating() {
        return starRating;
    }

    public void setStarRating(double starRating) {
        this.starRating = starRating;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public User getLendUser() {
        return lendUser;
    }

    public void setLendUser(User lendUser) {
        this.lendUser = lendUser;
    }

}
