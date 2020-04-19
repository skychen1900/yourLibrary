package sample.logic;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import sample.entity.LendHistory;
import sample.entity.Movie;
import sample.entity.User;

@RunWith(JUnit4.class)
public class MovieManagerTest {

	private EntityManager em;
	private final MovieManagerImpl movieManagerImpl = new MovieManagerImpl();
	private final UserManagerImpl userManagerImpl = new UserManagerImpl();

	@Before
	public void setUp() throws Exception {
		//
		em = Persistence.createEntityManagerFactory("YourLibraryTest").createEntityManager();
		movieManagerImpl.setEm(em);
		userManagerImpl.setEm(em);
	}

	@After
	public void tearDown() throws Exception {
		em.close();
	}

	@Test
	public void testCRUDforMovie() {
		//検索
		final List<Movie> list1st = movieManagerImpl.findAll();
		final int list1stSize = list1st.size();

		//新規作成
		final Movie movie = movieManagerImpl.createMovie("movie1title");
		assertEquals("movie1title", movie.getTitle());
		assertFalse(movie.getIsLent());
		//ID検索
		Movie find1 = movieManagerImpl.findById(movie.getId());
		assertEquals("movie1title", find1.getTitle());

		//更新
		find1.setCategory("movie1category");
		find1.setOutline("movie1outline");
		find1.setIsLent(true);
		movieManagerImpl.updateMovie(find1);

		//タイトル検索
		find1 = movieManagerImpl.findByTitle("movie1title");
		assertEquals("movie1title", find1.getTitle());
		assertEquals("movie1category", find1.getCategory());
		assertEquals("movie1outline", find1.getOutline());
		assertTrue(find1.getIsLent());

		//追加の件数確認
		final List<Movie> list2nd = movieManagerImpl.findAll();
		assertEquals(list1stSize + 1, list2nd.size());

		//削除
		movieManagerImpl.removeMovie(find1);
		find1 = movieManagerImpl.findByTitle("movie1title");
		assertNull(find1);

		//削除の件数確認
		final List<Movie> list3rd = movieManagerImpl.findAll();
		assertEquals(list1stSize, list3rd.size());
	}

	@Test
	public void testCRUDforLendHistory() {

		Movie movie = null;
		User user = null;
		try {
			//movieの新規作成
			movie = movieManagerImpl.createMovie("movie2title");
			//userの新規作成
			user = userManagerImpl.createUser("user2", "user2");

			//検索
			final List<LendHistory> list1st = movieManagerImpl.findAllLendHistory();
			final int list1stSize = list1st.size();

			//作成（貸出）
			final LendHistory history1 = movieManagerImpl.lendMovie(movie, user);
			assertEquals(movie.getId(), history1.getMovie().getId());
			assertEquals(user.getId(), history1.getLendUser().getId());
			assertNotNull(history1.getLendDate());

			//追加の件数確認
			final List<LendHistory> list2nd = movieManagerImpl.findAllLendHistory();
			final int list2ndSize = list2nd.size();
			assertEquals(list1stSize + 1, list2ndSize);

			//更新
			history1.setReview("nice");
			history1.setStarRating(5);
			history1.setDueDate(new Date());
			movieManagerImpl.updateLendHistory(history1);
			LendHistory history2 = movieManagerImpl.findLendHistoryById(history1.getId());
			assertEquals("nice", history2.getReview());
			assertEquals(new Double(5), new Double(history2.getStarRating()));
			assertNotNull(history2.getDueDate());

			//Movie側からの取得
			final Movie movie1 = movieManagerImpl.findById(movie.getId());
			assertEquals(history1.getId(), movie1.getLendHistories().get(0).getId());
			//User側からの取得
			final User user1 = userManagerImpl.findById(user.getId());
			assertEquals(history1.getId(), user1.getLendHistories().get(0).getId());

			//返却
			history2 = movieManagerImpl.returnMovie(history2);
			assertNotNull(history2.getReturnDate());

			//2件目の作成
			final LendHistory history3 = movieManagerImpl.lendMovie(movie, user);
			assertEquals(movie.getId(), history3.getMovie().getId());
			assertEquals(user.getId(), history3.getLendUser().getId());
			assertNotNull(history3.getLendDate());

			//１件削除
			movieManagerImpl.removeLendHistory(history1);
			final List<LendHistory> list3rd = movieManagerImpl.findAllLendHistory();
			final int list3rdSize = list3rd.size();
			assertEquals(list1stSize + 1, list3rdSize);

			//Userを削除した際に削除されていないことの確認
			userManagerImpl.removeUser(user);
			user = null;
			final List<LendHistory> list4th = movieManagerImpl.findAllLendHistory();
			final int list4thSize = list4th.size();
			assertEquals(list1stSize + 1, list4thSize);

			//Movieを削除した際に削除されることの確認
			movieManagerImpl.removeMovie(movie);
			movie = null;
			final List<LendHistory> list5th = movieManagerImpl.findAllLendHistory();
			final int list5thSize = list5th.size();
			assertEquals(list1stSize, list5thSize);
		} finally {
			//movieの削除
			if (movie != null) {
				movieManagerImpl.removeMovie(movie);
			}
			//userの削除
			if (user != null) {
				userManagerImpl.removeUser(user);
			}
		}
	}
}
