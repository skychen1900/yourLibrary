package sample.logic;

import java.util.List;

import sample.entity.User;

public interface UserManager {

	public User findById(long id);

	public User createUser(String account, String name);

	public List<User> findAll();

	public User updateUser(User find1);

	public User findByAccount(String string);

	public User login(String string, String string2);

	public boolean removeUser(User login);

	public int removeUser(List<User> users);

}
