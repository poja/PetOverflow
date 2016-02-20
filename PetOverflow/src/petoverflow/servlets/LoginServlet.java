package petoverflow.servlets;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import petoverflow.dao.UserDao;
import petoverflow.dao.derby.UserDaoDerby;
import petoverflow.dto.AuthenticationDto;

public class LoginServlet extends HttpServlet {

	private UserDao m_userDao;

	private static final long serialVersionUID = 1L;

	public LoginServlet() {
		super();
		m_userDao = UserDaoDerby.getInstance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		Gson gson = new Gson();

		// Parse the authentication information
		HashMap<String, Object> params = ServletUtility.getRequestParameters(request);

		String username = params.get(ParametersConfig.USERNAME).toString().toLowerCase();
		String password = params.get(ParametersConfig.PASSWORD).toString();
		if (username == null || password == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			Integer userId = m_userDao.isAuthenticationPair(username, password);
			if (userId != null) {
				AuthenticationDto auth = new AuthenticationDto(userId, username, password, true);
				response.getWriter().write(gson.toJson(auth));
				Cookie usernameCookie = new Cookie(ParametersConfig.USERNAME, auth.getUsername());
				Cookie passwordCookie = new Cookie(ParametersConfig.PASSWORD, auth.getPassword());
				Cookie idCookie = new Cookie("userId", userId.toString());
				response.addCookie(usernameCookie);
				response.addCookie(passwordCookie);
				response.addCookie(idCookie);
			} else {
				AuthenticationDto auth = new AuthenticationDto(-1, username, password, false);
				response.getWriter().write(gson.toJson(auth));
			}
		} catch (Exception e) {
			e.printStackTrace();
			Cookie usernameCookie = new Cookie(ParametersConfig.USERNAME, "");
			Cookie passwordCookie = new Cookie(ParametersConfig.PASSWORD, "");
			Cookie idCookie = new Cookie(ParametersConfig.USER_ID, "");
			response.addCookie(usernameCookie);
			response.addCookie(passwordCookie);
			response.addCookie(idCookie);
			AuthenticationDto auth = new AuthenticationDto(-1, username, password, false);
			response.getWriter().write(gson.toJson(auth));
		}
	}

}
