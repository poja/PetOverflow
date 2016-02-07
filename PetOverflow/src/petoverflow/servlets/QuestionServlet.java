package petoverflow.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import petoverflow.authentication.AuthenticatedHttpServlet;
import petoverflow.dao.Answer;
import petoverflow.dao.AnswerDao;
import petoverflow.dao.Question;
import petoverflow.dao.QuestionDao;
import petoverflow.dao.QuestionVoteDao;
import petoverflow.dao.User;
import petoverflow.dao.Vote;
import petoverflow.dao.Vote.VoteType;
import petoverflow.dao.derby.AnswerDaoDerby;
import petoverflow.dao.derby.QuestionDaoDerby;
import petoverflow.dao.derby.QuestionVoteDaoDerby;
import petoverflow.dto.AnswerDto;
import petoverflow.dto.QuestionDto;

/**
 * The QuestionServlet class is a servlet that provide a set of services to
 * create, read and search questions submitted by users.
 */
public class QuestionServlet extends AuthenticatedHttpServlet {

	private QuestionDao m_questionDao;

	private QuestionVoteDao m_questionVoteDao;

	private AnswerDao m_answerDao;

	private static final long serialVersionUID = 1L;

	public QuestionServlet() {
		super();
		m_questionDao = QuestionDaoDerby.getInstance();
		m_questionVoteDao = QuestionVoteDaoDerby.getInstance();
		m_answerDao = AnswerDaoDerby.getInstance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * petoverflow.authentication.AuthenticatedHttpServlet#doAuthenticatedPut(
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, petoverflow.dao.User)
	 */
	@Override
	protected void doAuthenticatedPut(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
		// Map this request to:
		// - /question/<question#>/vote

		String path = request.getPathInfo();
		int index = path.indexOf('/');
		if (index != 0) {
			throw new ServletException("Invalid URI");
		}
		path = path.substring(index + 1);
		index = path.indexOf('/');
		if (!path.substring(0, index).equals("question")) {
			throw new ServletException("Invalid URI");
		}
		path = path.substring(index + 1);
		index = path.indexOf('/');
		if (!path.substring(index + 1).equals("vote")) {
			throw new ServletException("Invalid URI");
		}
		int questionId;
		try {
			questionId = Integer.parseInt(path.substring(0, index));
		} catch (NumberFormatException e) {
			throw new ServletException("Invalid URI");
		}

		VoteType voteType = null; // TODO
		try {
			m_questionVoteDao.addVote(questionId, new Vote(user.getId(), voteType));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * petoverflow.authentication.AuthenticatedHttpServlet#doAuthenticatedPost(
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, petoverflow.dao.User)
	 */
	@Override
	protected void doAuthenticatedPost(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
		// Map this request to:
		// - /question

		String path = request.getPathInfo();
		int index = path.indexOf('/');
		if (index != 0) {
			throw new ServletException("Invalid URI");
		}
		path = path.substring(index + 1);
		if (!path.equals("question")) {
			throw new ServletException("Invalid URI");
		}

		String text = null;
		List<String> topics = null;
		// Get parameters TODO

		try {
			m_questionDao.createQuestion(text, user.getId(), topics);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * petoverflow.authentication.AuthenticatedHttpServlet#doAuthenticatedGet(
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, petoverflow.dao.User)
	 */
	@Override
	protected void doAuthenticatedGet(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
		// Map this request to:
		// - /question/newest
		// - /question/<question#>
		// - /question/<question#>/answers

		String path = request.getPathInfo();
		int index = path.indexOf('/');
		if (index != 0) {
			throw new ServletException("Invalid URI");
		}
		path = path.substring(index + 1);
		index = path.indexOf('/');
		if (index < 0) {
			throw new ServletException("Invalid URI");
		}
		if (!path.substring(0, index).equals("question")) {
			throw new ServletException("Invalid URI");
		}
		path = path.substring(index + 1);
		index = path.indexOf('/');
		if (index < 0) {
			if (path.equals("newest")) {
				getNewestQuestions(request, response, user);
				return;
			} else {
				int questionId;
				try {
					questionId = Integer.parseInt(path);
				} catch (NumberFormatException e) {
					throw new ServletException("Invalid URI");
				}
				getAQuestion(request, response, user, questionId);
			}
		} else {
			int questionId;
			try {
				questionId = Integer.parseInt(path.substring(0, index));
			} catch (NumberFormatException e) {
				throw new ServletException("Invalid URI");
			}
			if (!path.substring(index + 1).equals("answers")) {
				throw new ServletException("Invalid URI");
			}
			getBestAnswers(request, response, user, questionId);
		}
	}

	/**
	 * Get data about a question
	 * 
	 * @param request
	 *            the request from the client
	 * @param response
	 *            the response object
	 * @param user
	 *            the current logged in user
	 * @param questionId
	 *            the requested question's id
	 * @throws ServletException
	 *             if DAO fail
	 */
	private void getAQuestion(HttpServletRequest request, HttpServletResponse response, User user, int questionId)
			throws ServletException {
		try {
			Question question = m_questionDao.getQuestion(questionId);
			QuestionDto questionDto = question.toQuestionDto(user.getId());

			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			Gson gson = new Gson();
			out.append(gson.toJson(questionDto));

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		}
	}

	/**
	 * Get the best answers to a question
	 * 
	 * @param request
	 *            the request from the client
	 * @param response
	 *            the response object
	 * @param user
	 *            the current logged in user
	 * @param questionId
	 *            the question's id
	 * @throws ServletException
	 *             if DAO fail
	 * @throws IOException
	 *             if failed to write back to client
	 */
	private void getBestAnswers(HttpServletRequest request, HttpServletResponse response, User user, int questionId)
			throws ServletException, IOException {
		try {
			List<Answer> answers = m_answerDao.getQuestionAnswers(questionId);
			List<AnswerDto> answersDto = new ArrayList<AnswerDto>();
			for (Answer answer : answers) {
				AnswerDto answerDto = answer.toAnswerDto(user.getId());
				answersDto.add(answerDto);
			}

			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			Gson gson = new Gson();
			out.append(gson.toJson(answersDto));

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		}
	}

	/**
	 * Get the newest question
	 * 
	 * @param request
	 *            the request from the client
	 * @param response
	 *            the response object
	 * @param user
	 *            the current logged in user
	 * @throws ServletException
	 *             if DAO fail
	 * @throws IOException
	 *             if fail to write to client
	 */
	private void getNewestQuestions(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
		List<QuestionDto> ans = new ArrayList<QuestionDto>();
		try {
			List<Question> newestQuestions = m_questionDao.getNewestQuestions(20);
			for (Question question : newestQuestions) {
				try {
					QuestionDto questionDto = question.toQuestionDto(user.getId());
					ans.add(questionDto);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		}

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		out.append(gson.toJson(ans));
	}

}
