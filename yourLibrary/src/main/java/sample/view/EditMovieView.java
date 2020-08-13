package sample.view;

import java.io.File;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;
import sample.entity.Movie;
import sample.logic.MovieManager;
import sample.util.interceptor.WithLog;
import sample.view.dto.MovieScreenDto;
import sample.view.util.ViewUtil;

@Named
@ViewScoped
@WithLog
/*
 * Edit User BackingBean Class
 */
public class EditMovieView implements Serializable {

    private static final long serialVersionUID = -7001108729176075057L;

    @Getter
    @Setter
    private MovieScreenDto newMovieDto = new MovieScreenDto();//新規映画情報定義DTO

    @Getter
    @Setter
    private UploadedFile file;

    //	@Getter
    //	@Setter
    //	private IdEntityListDataModel<User> usersListDto;// ユーザー表示一覧の全てユーザーの
    //
    //	@Getter
    //	@Setter
    //	private List<User> selectedUsers;//選択分ユーザー
    //
    //	private boolean isSelected;

    private static final Logger logger = LoggerFactory.getLogger(EditMovieView.class);

    @Getter
    @Setter
    private String newPassword;

    @Inject
    private MovieManager movieManager;

    @PostConstruct
    public void init() {
        //		List<User> users = userManager.findAll();
        //		usersListDto = new IdEntityListDataModel<User>(users);
    }

    public void addMovie() {

        File fileX = new File(file.getFileName());
        logger.debug("Succesful: fileName={}, filePath={} is uploaded.", file.getFileName(), fileX.getPath());

        Movie movie = movieManager.createMovie(newMovieDto.getTitle());
        movie.setOutline(newMovieDto.getOutline());
        movie.setImage(file.getFileName());
        movie.setIsLent(newMovieDto.isLend());
        movie.setImageData(file.getContent());
        movie = movieManager.updateMovie(movie);

        ViewUtil.AddMessage("映画の追加", "映画を追加しました。");

        //TODO  映画追加中の画面動画を追加

        return;
    }

    public void upload() {
        ViewUtil.AddMessage("Succesful", file.getFileName() + " is uploaded.");
    }

    public void handleFileUpload(FileUploadEvent event) {
        ViewUtil.AddMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
    }

}