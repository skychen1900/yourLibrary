package sample.view;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import sample.entity.Movie;
import sample.logic.MovieManager;

@Named
@ApplicationScoped
public class ImageStreamerUtilBean {

    @Inject
    private MovieManager movieManager;

    public byte[] getById(Long id) {
        Movie m = movieManager.findById(id);
        return m.getImageData();
    }
}
