package sample.view.dto;

import lombok.Getter;
import lombok.Setter;
import sample.util.interceptor.WithLog;

@WithLog
public class MovieScreenDto {

	@Getter
	@Setter
	private String title;

	@Getter
	@Setter
	private String outline;

	@Getter
	@Setter
	private String category;

	@Getter
	@Setter
	private String image;

	private boolean lend;

	public boolean isLend() {
		return lend;
	}

	public void setLend(boolean lend) {
		this.lend = lend;
	}

}