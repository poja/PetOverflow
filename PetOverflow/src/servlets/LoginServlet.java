package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import dao.UserDao;
import dao.UserDaoDerby;
import dto.AuthenticationDto;

/**
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
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
		}
		
		UserDao dao = new UserDaoDerby();
		if (dao.isAuthenticationPair(auth.getUsername(), auth.getPassword())) {
			auth.setSuccess(true);
			response.getWriter().write(gson.toJson(auth));
			Cookie usernameCookie = new Cookie("username", auth.getUsername());
			Cookie passwordCookie = new Cookie("password", auth.getPassword());
			response.addCookie(usernameCookie);
			response.addCookie(passwordCookie);
		}
		else {
			auth.setSuccess(false);
			response.getWriter().write(gson.toJson(auth));
		}
	}

}
