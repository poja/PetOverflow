package servlets;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import authentication.Authenticator;
import dao.QuestionDao;
import dao.QuestionDaoMock;

/**
 * Servlet implementation class UserServlet
 */
public class RecentQuestionsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Gson gson;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RecentQuestionsServlet() {
		super();
		gson = new Gson();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json");
		Gson gson = new Gson();

		if (Authenticator.isAuthenticated(request)) {
			QuestionDao dao = new QuestionDaoMock();
			ArrayList<String> ans = dao.getNewestQuestions(20);
			response.getWriter().append(gson.toJson(ans));
		}
		else {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}
		
	}

}
