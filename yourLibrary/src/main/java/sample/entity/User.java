package sample.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "USERTBL")
// ユーザエンティティ
public class User implements IdEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;
    @Column(name = "ACCOUNT", nullable = false, unique = true)
    private String account;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "NAME", nullable = false)
    private String name;
    @Column(name = "E_MAIL")
    private String email;

    //管理者か否か
    @Column(name = "IS_ADMIN")
    private boolean isAdmin = false;

    @Column(name = "THEME")
    @Getter
    @Setter
    private String theme;

    //ユーザが借りた『貸出履歴』コレクション
    @OneToMany(mappedBy = "lendUser")
    @JsonManagedReference //To avoid  [JsonMappingException: Infinite recursion]
    List<LendHistory> lendHistories;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public List<LendHistory> getLendHistories() {
        return lendHistories;
    }

    public void setLendHistories(List<LendHistory> lendHistories) {
        this.lendHistories = lendHistories;
    }

}