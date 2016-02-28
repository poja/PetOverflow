package petoverflow.servlets;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import petoverflow.dao.UserDao;
import petoverflow.dao.derby.UserDaoDerby;
import petoverflow.dto.AuthenticationDto;

/**
 * This servlet allows logging in to the system.
 * Uses HttpSession to save user credentials
 */
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
		HttpSession session = request.getSession();

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
				session.setAttribute("username", username);
				session.setAttribute("password", password);
				session.setAttribute("userId", userId);
			} else {
				AuthenticationDto auth = new AuthenticationDto(-1, username, password, false);
				response.getWriter().write(gson.toJson(auth));
			}
		} catch (Exception e) {
			e.printStackTrace();
			session.removeAttribute("username");
			session.removeAttribute("password");
			session.removeAttribute("userId");
			AuthenticationDto auth = new AuthenticationDto(-1, username, password, false);
			response.getWriter().write(gson.toJson(auth));
		}
	}

}
