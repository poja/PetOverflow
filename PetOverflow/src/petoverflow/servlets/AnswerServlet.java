package petoverflow.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import petoverflow.authentication.AuthenticatedHttpServlet;
import petoverflow.dao.Answer;
import petoverflow.dao.AnswerDao;
import petoverflow.dao.AnswerVoteDao;
import petoverflow.dao.User;
import petoverflow.dao.Vote;
import petoverflow.dao.Vote.VoteType;
import petoverflow.dao.derby.AnswerDaoDerby;
import petoverflow.dao.derby.AnswerVoteDaoDerby;
import petoverflow.dto.AnswerDto;

/**
 * The AnswerServlet class is a servlet that provide a set of services to
 * create, read and search answers to questions by users.
 */
public class AnswerServlet extends AuthenticatedHttpServlet {

	private AnswerDao m_answerDao;

	private AnswerVoteDao m_answerVoteDao;

	private static final long serialVersionUID = 1L;

	public AnswerServlet() {
		super();
		m_answerDao = AnswerDaoDerby.getInstance();
		m_answerVoteDao = AnswerVoteDaoDerby.getInstance();
	}

	/**
	 * Do Authenticated Put
	 * 
	 * Authenticated version of {@link HttpServlet#doPut}
	 */
	protected void doAuthenticatedPut(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
		// Map this request to:
		// - /answer/<answer#>/vote

		String path = request.getPathInfo();
		int index = path.indexOf('/');
		if (index != 0) {
			throw new ServletException("Invalid URI");
		}
		path = path.substring(index + 1);
		index = path.indexOf('/');
		if (!path.substring(0, index).equals("answer")) {
			throw new ServletException("Invalid URI");
		}
		path = path.substring(index + 1);
		index = path.indexOf('/');
		if (!path.substring(index + 1).equals("vote")) {
			throw new ServletException("Invalid URI");
		}
		int answerId;
		try {
			answerId = Integer.parseInt(path.substring(0, index));
		} catch (NumberFormatException e) {
			throw new ServletException("Invalid URI");
		}

		VoteType voteType = null; // TODO
		try {
			m_answerVoteDao.addVote(answerId, new Vote(user.getId(), voteType));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException();
		}
	}

	/**
	 * Do authenticated post
	 * 
	 * Authenticated version of {@link HttpServlet#doPost}
	 */
	protected void doAuthenticatedPost(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
		// Map this request to:
		// - /answer

		String path = request.getPathInfo();
		int index = path.indexOf('/');
		if (index != 0) {
			throw new ServletException("Invalid URI");
		}
		path = path.substring(index + 1);
		if (!path.equals("answer")) {
			throw new ServletException("Invalid URI");
		}

		// read parameters TODO
		String text = null;
		int questionId = 0;
		try {
			m_answerDao.createAnswer(text, user.getId(), questionId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		}
	}

	/**
	 * Do authenticated get
	 * 
	 * Authenticated version of {@link HttpServlet#doGet}
	 */
	protected void doAuthenticatedGet(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
		// Map this request to:
		// - /answer/<answer#>

		String path = request.getPathInfo();
		int index = path.indexOf('/');
		if (index != 0) {
			throw new ServletException("Invalid URI");
		}
		path = path.substring(index + 1);
		index = path.indexOf('/');
		if (!path.substring(0, index).equals("answer")) {
			throw new ServletException("Invalid URI");
		}
		path = path.substring(index + 1);

		int answerId;
		try {
			answerId = Integer.parseInt(path);
		} catch (NumberFormatException e) {
			throw new ServletException("Invalid URI");
		}

		Answer answer;
		AnswerDto answerDto;
		try {
			answer = m_answerDao.getAnswer(answerId);
			answerDto = answer.toAnswerDto(user.getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		}

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		out.append(gson.toJson(answerDto));
	}

}
