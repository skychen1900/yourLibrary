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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import sample.entity.Movie;

@RunWith(JUnit4.class)
public class MovieManagerTest {

    private EntityManager em;
    private final MovieManagerImpl movieManagerImpl = new MovieManagerImpl();
    private final UserManagerImpl userManagerImpl = new UserManagerImpl();
    private EntityTransaction tx;

    @Before
    public void setUp() throws Exception {
        //
        em = Persistence.createEntityManagerFactory("yourlibrary-test").createEntityManager();
        tx = em.getTransaction();
        tx.begin();
        movieManagerImpl.setEm(em);
        userManagerImpl.setEm(em);
    }

    @After
    public void tearDown() throws Exception {
        tx.commit();
        em.close();
    }

    @Test
    public void testCRUDforMovie1() throws JsonProcessingException {

        //新規作成
        Movie movie = this.createMovieData("movie1title");
        assertEquals("movie1title", movie.getTitle());
        assertFalse(movie.getIsLent());

        // JSON Obejct Print
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String json = mapper.writeValueAsString(movie);
        System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
        System.out.println(json);

    }

    private Movie createMovieData(String title) {
        return movieManagerImpl.createMovie(title);
    }

    @Test
    public void testCRUDforMovie2() {
        //ID検索
        Movie movie = this.createMovieData("movie2title");

        Movie find1 = movieManagerImpl.findById(movie.getId());
        assertEquals("movie2title", find1.getTitle());

        //更新
        find1.setCategory("movie2category");
        find1.setOutline("movie2outline");
        find1.setIsLent(true);
        movieManagerImpl.updateMovie(find1);
    }

    @Test
    public void testCRUDforMovie3() {

        //タイトル検索
        Movie find1 = movieManagerImpl.findByTitle("movie2title");
        assertEquals("movie2title", find1.getTitle());
        assertEquals("movie2category", find1.getCategory());
        assertEquals("movie2outline", find1.getOutline());
        assertTrue(find1.getIsLent());

    }

    @Test
    public void testCRUDforMovie4() {

        //追加の件数確認
        final List<Movie> list2nd = movieManagerImpl.findAll();
        assertEquals(2, list2nd.size());

        //削除
        for (Movie movie : list2nd) {
            movieManagerImpl.removeMovie(movie);
        }

    }

    @Test
    public void testCRUDforMovie5() {

        //削除
        Movie find1 = movieManagerImpl.findByTitle("movie1title");
        assertNull(find1);
        Movie find2 = movieManagerImpl.findByTitle("movie2title");
        assertNull(find2);

    }

}
