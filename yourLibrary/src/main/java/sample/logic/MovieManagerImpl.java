package sample.logic;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import lombok.Getter;
import lombok.Setter;
import sample.entity.LendHistory;
import sample.entity.Movie;
import sample.entity.User;
import sample.util.Service;

@Service
public class MovieManagerImpl implements MovieManager {

    @PersistenceContext(unitName = "yourlibrary")
    @Getter
    @Setter
    private EntityManager em;

    @Override
    public Movie createMovie(String title) {
        final Movie movie = new Movie();
        movie.setTitle(title);
        //		EntityManager em = getEm();
        //		final EntityTransaction tx = em.getTransaction();
        //		tx.begin();
        em.persist(movie);
        //		tx.commit();
        //		em.close();
        return movie;
    }

    @Override
    public Movie findById(long id) {
        //		EntityManager em = getEm();
        final Movie movie = em.find(Movie.class, id);
        em.clear();
        //		em.close();
        return movie;
    }

    @Override
    public List<Movie> findAll() {
        //		EntityManager em = getEm();
        final TypedQuery<Movie> q = em.createQuery("select m from Movie m order by m.title asc", Movie.class);
        final List<Movie> result = q.getResultList();
        //		em.close();
        return result;
    }

    @Override
    public Movie findByTitle(String title) {
        //		EntityManager em = getEm();
        final TypedQuery<Movie> q = em.createQuery("select m from Movie m where m.title=:title", Movie.class);
        q.setParameter("title", title);
        final List<Movie> result = q.getResultList();
        //		em.close();
        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    @Override
    public Movie updateMovie(Movie movie) {
        //		EntityManager em = getEm();
        //		final EntityTransaction tx = em.getTransaction();
        //		tx.begin();
        if (!em.contains(movie)) {
            movie = em.merge(movie);
        }
        //		tx.commit();
        //		em.close();
        return movie;
    }

    @Override
    public boolean removeMovie(Movie movie) {
        //		EntityManager em = getEm();
        final Movie find = em.find(Movie.class, movie.getId());
        if (find == null) {
            return false;
        }
        //		final EntityTransaction tx = em.getTransaction();
        //		tx.begin();
        em.remove(find);
        //		tx.commit();
        //		em.close();
        return true;
    }

    @Override
    public LendHistory lendMovie(Movie movie, User user) {
        final LendHistory history = new LendHistory();
        history.setLendDate(Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
        //返却日計算：
        //  カレント日＋１週間
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_WEEK, 7);
        history.setDueDate(calendar.getTime());

        history.setMovie(movie);
        history.setLendUser(user);
        //		EntityManager em = getEm();
        //		final EntityTransaction tx = em.getTransaction();
        //		tx.begin();
        em.persist(history);
        if (!em.contains(movie)) {
            movie = em.merge(movie);
        }
        movie.setIsLent(true);
        em.persist(movie);

        final ArrayList<LendHistory> historys = new ArrayList<LendHistory>();
        historys.add(history);
        movie.setLendHistories(historys);
        if (!em.contains(user)) {
            user = em.merge(user);
        }
        user.setLendHistories(historys);
        //		tx.commit();
        //		em.close();
        return history;
    }

    @Override
    public LendHistory updateLendHistory(LendHistory history) {
        //		EntityManager em = getEm();
        //		final EntityTransaction tx = em.getTransaction();
        //		tx.begin();
        if (!em.contains(history)) {
            history = em.merge(history);
        }
        //		tx.commit();
        //		em.close();
        return history;
    }

    @Override
    public LendHistory returnMovie(LendHistory history) {
        //		EntityManager em = getEm();
        final EntityTransaction tx = em.getTransaction();
        tx.begin();
        if (!em.contains(history)) {
            history = em.merge(history);
        }
        history.setReturnDate(Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
        Movie movie = history.getMovie();
        if (!em.contains(movie)) {
            movie = em.merge(movie);
        }
        movie.setIsLent(false);
        tx.commit();
        //		em.close();
        return history;
    }

    @Override
    public boolean removeLendHistory(LendHistory history) {
        //		EntityManager em = getEm();
        final LendHistory find = em.find(LendHistory.class, history.getId());
        if (find == null) {
            return false;
        }
        final EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.remove(find);
        tx.commit();
        //		em.close();
        return true;
    }

    @Override
    public LendHistory findLendHistoryById(long id) {
        //		EntityManager em = getEm();
        final LendHistory history = em.find(LendHistory.class, id);
        em.clear();
        //		em.close();
        return history;
    }

    @Override
    public List<LendHistory> findAllLendHistory() {
        final TypedQuery<LendHistory> q = em.createQuery("select h from LendHistory h order by h.movie.title asc",
                LendHistory.class);
        final List<LendHistory> result = q.getResultList();
        //		em.close();
        return result;
    }

    @Override
    public List<LendHistory> findAllLendHistoryByUser(Long lendUserId) {
        final TypedQuery<LendHistory> q = em.createQuery(
                "select h from LendHistory h where h.lendUser.id =:lendUserId order by h.movie.title asc",
                LendHistory.class);
        q.setParameter("lendUserId", lendUserId);
        final List<LendHistory> result = q.getResultList();
        //		em.close();
        return result;
    }

    @Override
    public List<String> getEnteredTitles() {
        TypedQuery<String> q = em.createQuery("select distinct m.title from Movie m order by m.title asc",
                String.class);
        List<String> result = q.getResultList();
        return result;
    }

    @Override
    public List<String> getEnteredCategory() {
        TypedQuery<String> q = em.createQuery("select distinct m.category from Movie m order by m.category asc",
                String.class);
        List<String> result = q.getResultList();
        return result;
    }

    @Override
    public List<String> getEnteredOutline() {
        TypedQuery<String> q = em.createQuery("select distinct m.outline from Movie m order by m.outline asc",
                String.class);
        List<String> result = q.getResultList();
        return result;
    }

    //Criteria APIを使ったsearchMovieメソッド
    // http://tshix.hatenablog.com/entry/2015/08/04/010443
    @Override
    public List<Movie> searchMovie(String title, String category, String outline, Boolean isLent) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Movie> query = cb.createQuery(Movie.class);
        Root<Movie> root = query.from(Movie.class);
        query.select(root);

        List<Predicate> predicates = new ArrayList<Predicate>();
        if (title != null && !title.isEmpty()) {
            predicates.add(cb.like(root.get("title"), "%" + title + "%"));
        }
        if (category != null && !category.isEmpty()) {
            predicates.add(cb.equal(root.get("category"), category));
        }
        if (outline != null && !outline.isEmpty()) {
            predicates.add(cb.like(root.get("outline"), "%" + outline + "%"));
        }
        if (isLent != null) {
            predicates.add(cb.equal(root.get("isLent"), isLent));
        }

        if (predicates.size() > 0) {
            query.where(cb.and(predicates.toArray(new Predicate[] {})));
        }
        query.orderBy(cb.asc(root.get("title")));
        return em.createQuery(query).getResultList();
    }

}
