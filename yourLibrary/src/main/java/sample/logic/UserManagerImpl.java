package sample.logic;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
//import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import lombok.Getter;
import lombok.Setter;
import sample.entity.LendHistory;
import sample.entity.User;
import sample.util.Service;

@Service
@ApplicationScoped
public class UserManagerImpl implements UserManager {

	// JPAのコンテナ管理永続性コンテキスト
	@PersistenceContext(unitName = "yourlibrary")
	@Getter
	@Setter
	private EntityManager em;

	@PostConstruct
	public void postConstruct() {
		System.out.println("[Application Scope] post construct : " + hashCode());
	}

	@PreDestroy
	public void preDestroy() {
		System.out.println("[Application Scope] pre construct : " + hashCode());
	}

	//１．アプリケーションで管理するEntityManger
	//Deployとき、Containerより管理
	//Testとき、Testより手動実施

	//２．persistで新規オブジェクトの永続化
	@Override
	public User createUser(String account, String name) {
		final User user = new User();
		user.setAccount(account);
		user.setName(name);
		em.persist(user);//newしたオブジェクトの永続化
		return user;
	}

	//３．findの使用
	@Override
	public User findById(long id) {
		final User user = em.find(User.class, id);
		em.clear();
		return user;
	}

	//４．もっとも簡単なJPQL
	@Override
	public List<User> findAll() {
		//		EntityManager em = getEm();
		//		TypedQuery<User> q = em.createQuery("select u from User u order by u.account asc", User.class);
		//		List<User> result = q.getResultList();
		final List<User> result = em.createNamedQuery("findUserEntityAll", User.class).getResultList();
		return result;
	}

	//５．JPQLの名前付きパラメータの使用
	@Override
	public User findByAccount(String account) {
		final TypedQuery<User> q = em.createQuery("select u from User u where u.account=:account", User.class);
		q.setParameter("account", account);//名前付きパラメータ
		final List<User> result = q.getResultList();
		if (result.size() > 0) {
			return result.get(0);
		}
		return null;
	}

	//６．JPQLを使ったログイン処理
	@Override
	public User login(String account, String password) {
		final TypedQuery<User> q = em
				.createQuery("select u from User u where u.account=:account and u.password=:password", User.class);
		q.setParameter("account", account);
		q.setParameter("password", password);
		final List<User> result = q.getResultList();
		if (result.size() > 0) {
			return result.get(0);
		}

		if ("admin".equals(account) && "admin".equals(password)) {
			User admin = findByAccount(account);
			if (admin == null) {//adminがいないときのみadminユーザを作成するバックドア。
				admin = createUser(account, password);
				admin.setPassword("admin");
				admin.setIsAdmin(true);
				updateUser(admin);//冗長だが、Insert後すぐにUpdateしている。
				return admin;
			}
		}

		return null;
	}

	//７．デタッチ状態から永続コンテキストへマージ
	@Override
	public User updateUser(User user) {
		if (!em.contains(user)) {
			user = em.merge(user);//マージ
		}
		return user;
	}

	//８．エンティティの削除
	@Override
	public boolean removeUser(User user) {
		final User find = em.find(User.class, user.getId());
		if (find == null) {
			return false;
		}
		final List<LendHistory> histories = find.getLendHistories();
		for (final LendHistory history : histories) {
			history.setLendUser(null);//履歴からは、ユーザをnullにしてから削除する。
			em.merge(history);//履歴の更新
		}
		em.remove(find);//削除
		return true;
	}

	@Override
	public int removeUser(List<User> users) {

		int removeCount = 0;
		for (User user : users) {
			User find = em.find(User.class, user.getId());
			if (find == null) {
				continue;
			}
			List<LendHistory> histories = find.getLendHistories();
			for (LendHistory history : histories) {
				history.setLendUser(null);//履歴からは、ユーザをnullにしてから削除する。
				em.merge(history);//履歴の更新
			}
			em.remove(find);
			removeCount++;
		}
		return removeCount;
	}

}
