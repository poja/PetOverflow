package petoverflow.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import petoverflow.dao.UserDao;
import petoverflow.dao.derby.UserDaoDerby;
import petoverflow.dto.AuthenticationDto;

/**
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet {

	private UserDao m_userDao;

	private static final long serialVersionUID = 1L;

	public LoginServlet() {
		super();
		m_userDao = UserDaoDerby.getInstance();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json");
		Gson gson = new Gson();

		
		// Parse the authentication information
		StringBuilder sb = new StringBuilder();
		String s;
		while ((s = request.getReader().readLine()) != null) {
			sb.append(s);
		}
		AuthenticationDto auth = gson.fromJson(sb.toString(), AuthenticationDto.class);

		if (auth == null || auth.getUsername() == null || auth.getPassword() == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			if (m_userDao.isAuthenticationPair(auth.getUsername(), auth.getPassword())) {
				auth.setSuccess(true);
				response.getWriter().write(gson.toJson(auth));
				Cookie usernameCookie = new Cookie("username", auth.getUsername());
				Cookie passwordCookie = new Cookie("password", auth.getPassword());
				response.addCookie(usernameCookie);
				response.addCookie(passwordCookie);
			} else {
				auth.setSuccess(false);
				response.getWriter().write(gson.toJson(auth));
			}
		} catch (Exception e) {
			e.printStackTrace();
			auth.setSuccess(false);
			Cookie usernameCookie = new Cookie("username", "");
			Cookie passwordCookie = new Cookie("password", "");
			response.addCookie(usernameCookie);
			response.addCookie(passwordCookie);
			response.getWriter().write(gson.toJson(auth));
		}
	}

}
