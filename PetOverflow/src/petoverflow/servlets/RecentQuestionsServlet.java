package petoverflow.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import petoverflow.authentication.AuthenticatedHttpServlet;
import petoverflow.dao.Question;
import petoverflow.dao.QuestionDao;
import petoverflow.dao.derby.QuestionDaoDerby;
import petoverflow.dto.QuestionDto;

/**
 * Servlet implementation class UserServlet
 */
public class RecentQuestionsServlet extends AuthenticatedHttpServlet {

	private static final long serialVersionUID = 1L;

	private QuestionDao m_questionDao;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RecentQuestionsServlet() {
		super();
		m_questionDao = QuestionDaoDerby.getInstance();
	}

	protected void doAuthenticatedGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		Gson gson = new Gson();
		List<QuestionDto> ans = new ArrayList<QuestionDto>();
		try {
			List<Question> newestQuestions = m_questionDao.getNewestQuestions(20);
			for (Question question : newestQuestions) {
				try {
					QuestionDto questionDto = question.toQuestionDto();
					ans.add(questionDto);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		response.getWriter().append(gson.toJson(ans));
	}

}
