package sample.logic;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import sample.entity.User;
import sample.logic.UserManagerImpl;

@RunWith(JUnit4.class)
public class UserManagerTest {

	private EntityManager em;
	private final UserManagerImpl userManagerImpl = new UserManagerImpl();

	@Before
	public void setUp() throws Exception {
		em = Persistence.createEntityManagerFactory("YourLibraryTest").createEntityManager();
		userManagerImpl.setEm(em);
	}

	@After
	public void tearDown() throws Exception {
		em.close();
	}

	@Test
	public void testCRUD() {

		//新規作成
		final EntityTransaction tx = em.getTransaction();
		tx.begin();
		final User user1 = userManagerImpl.createUser("user1", "user1name");
		tx.commit();
		assertEquals("user1", user1.getAccount());
		assertEquals("user1name", user1.getName());

		//検索
		final List<User> list1st = userManagerImpl.findAll();
		final int list1stSize = list1st.size();

		//ID検索
		User find1 = userManagerImpl.findById(user1.getId());
		assertEquals("user1", find1.getAccount());
		assertEquals("user1name", find1.getName());

		//更新
		tx.begin();
		find1.setPassword("micky");
		find1.setEmail("micky@destiney.land.hell");
		find1.setIsAdmin(true);
		userManagerImpl.updateUser(find1);
		tx.commit();

		//アカウント名検索
		find1 = userManagerImpl.findByAccount("user1");
		assertEquals("user1", find1.getAccount());
		assertEquals("user1name", find1.getName());
		assertEquals("micky", find1.getPassword());
		assertEquals("micky@destiney.land.hell", find1.getEmail());
		assertTrue(find1.getIsAdmin());

		//ログイン
		final User login = userManagerImpl.login("user1", "micky");
		assertNotNull(login);
		assertEquals("user1name", login.getName());

		//追加の件数確認
		final List<User> list2nd = userManagerImpl.findAll();
		assertEquals(list1stSize, list2nd.size());

		//削除
		tx.begin();
		userManagerImpl.removeUser(login);
		find1 = userManagerImpl.findByAccount("user1");
		tx.commit();
		assertNull(find1);

		//削除の件数確認
		final List<User> list3rd = userManagerImpl.findAll();
		assertEquals(list1stSize - 1, list3rd.size());
	}
}
