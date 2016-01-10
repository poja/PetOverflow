package authentication;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import dao.UserDao;
import dao.UserDaoDerby;

public class Authenticator {

	/**
	 * Checks whether the client is authenticated.
	 * 
	 * @param request HTTP request, possibly with authentication cookies
	 * @return Whether the client has authenticated in the current session.
	 */
	public static boolean isAuthenticated(HttpServletRequest request) {
		
		// Get username and password from the cookies
		Cookie[] cookies = request.getCookies();
		String username = null,
				password = null;
		
		if (cookies == null)
			return false;
		
		for (Cookie c : cookies) {
			if (c.getName().equals("username"))
				username = c.getValue();
			else if (c.getName().equals("password"))
				password = c.getValue();
		}
		
		// Check if the username and password are valid, and if so, if they match
		if (username == null || password == null)
			return false;
		else if (!DataValidator.isValidUsername(username)) 
			return false;
		else if (!DataValidator.isValidPassword(username))
			return false;
		else {
			UserDao dao = new UserDaoDerby();
			return dao.isAuthenticationPair(username, password);
		}
		
	}
	
}
