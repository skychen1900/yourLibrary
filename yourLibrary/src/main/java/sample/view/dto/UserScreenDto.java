package sample.view.dto;

import lombok.Getter;
import lombok.Setter;
import sample.util.interceptor.WithLog;

@WithLog
public class UserScreenDto {

	@Getter
	@Setter
	private String account;

	@Getter
	@Setter
	private String name;

	@Getter
	@Setter
	private String password;

	@Getter
	@Setter
	private String email;

	@Getter
	@Setter
	private boolean admin;

}