package dao;

public class UserDaoMock implements UserDao {

	@Override
	public boolean isAuthenticationPair(String username, String password) {
		return username.equals("Yishai") && password.equals("Gronich");
	}

}
