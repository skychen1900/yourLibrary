package sample.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "MOVIE")
// 映画
public class Movie implements IdEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "OUTLINE")
    private String outline;

    @Column(name = "CATEGORY")
    private String category;

    @Column(name = "IS_LENT")
    private boolean isLent = false;

    @Column(name = "IMAGE")
    private String image;

    @Column(name = "IMG_DAT")
    @JsonIgnore
    private byte[] imageData;

    //ユーザが借りた『貸出履歴』コレクション。親のオブジェクトの操作は伝播させます
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    @JsonManagedReference
    List<LendHistory> lendHistories;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOutline() {
        return outline;
    }

    public void setOutline(String outline) {
        this.outline = outline;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean getIsLent() {
        return isLent;
    }

    public void setIsLent(boolean isLent) {
        this.isLent = isLent;
    }

    public List<LendHistory> getLendHistories() {
        return lendHistories;
    }

    public void setLendHistories(List<LendHistory> lendHistories) {
        this.lendHistories = lendHistories;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

}
