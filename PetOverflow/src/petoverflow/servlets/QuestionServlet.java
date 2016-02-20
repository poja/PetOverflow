package petoverflow.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import petoverflow.dao.items.Answer;
import petoverflow.dao.items.Question;
import petoverflow.dao.items.User;
import petoverflow.dao.items.Vote;
import petoverflow.dao.items.Vote.VoteType;
import petoverflow.dto.AnswerDto;
import petoverflow.dto.QuestionDto;

/**
 * The QuestionServlet class is a servlet that provide a set of services to
 * create, read and search questions submitted by users.
 */
public class QuestionServlet extends AuthenticatedHttpServlet {

	private static final long serialVersionUID = 1L;

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
		Pattern p = Pattern.compile("/question/([0-9]*)/vote");

		String path = ServletUtility.getPath(request);
		Matcher m = p.matcher(path);
		if (!m.find()) {
			throw new ServletException("Invalid URI");
		}
		int questionId = Integer.parseInt(m.group(1));

		HashMap<String, String> params = ServletUtility.getRequestParameters(request);
		String voteType = params.get(ParametersConfig.VOTE_TYPE);

		try {
			if (voteType.equals(ParametersConfig.VOTE_TYPE_NONE)) {
				m_daoManager.getQuestionVoteDao().removeVote(questionId, user.getId());
			} else if (voteType.equals(ParametersConfig.VOTE_TYPE_UP)) {
				m_daoManager.getQuestionVoteDao().addVote(questionId, new Vote(user.getId(), VoteType.Up));
			} else if (voteType.equals(ParametersConfig.VOTE_TYPE_DOWN)) {
				m_daoManager.getQuestionVoteDao().addVote(questionId, new Vote(user.getId(), VoteType.Down));
			}
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
		Pattern p = Pattern.compile("/question");

		String path = ServletUtility.getPath(request);
		Matcher m = p.matcher(path);
		if (!m.find()) {
			throw new ServletException("Invalid URI");
		}

		HashMap<String, String> params = ServletUtility.getRequestParameters(request);
		String text = params.get(ParametersConfig.TEXT);
		List<String> topics = ServletUtility.convertListFromJson(params.get(ParametersConfig.TOPICS));

		try {
			m_daoManager.getQuestionDao().createQuestion(text, user.getId(), topics);
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
		// - /question/best
		// - /question/<question#>
		// - /question/<question#>/answers
		Pattern p1 = Pattern.compile("/question/best");
		Pattern p2 = Pattern.compile("/question/newest");
		Pattern p3 = Pattern.compile("/question/([0-9]*)");
		Pattern p4 = Pattern.compile("/question/([0-9]*)/answers");

		String path = ServletUtility.getPath(request);
		Matcher m1 = p1.matcher(path);
		Matcher m2 = p2.matcher(path);
		Matcher m3 = p3.matcher(path);
		Matcher m4 = p4.matcher(path);
		if (m4.find()) {
			int questionId = Integer.parseInt(m3.group(1));
			getBestAnswers(request, response, user, questionId);
		} else if (m3.find()) {
			int questionId = Integer.parseInt(m2.group(1));
			getAQuestion(request, response, user, questionId);
		} else if (m2.find()) {
			getNewestQuestions(request, response, user);
		} else if (m1.find()) {
			getBestQuestion(request, response, user);
		} else {
			throw new ServletException("Invalid URI");
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
	 * @throws IOException
	 *             if failed to write back to client
	 */
	private void getAQuestion(HttpServletRequest request, HttpServletResponse response, User user, int questionId)
			throws ServletException, IOException {
		QuestionDto questionDto;
		try {
			Question question = m_daoManager.getQuestionDao().getQuestion(questionId);
			questionDto = question.toQuestionDto(user.getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		}

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		out.append(gson.toJson(questionDto));
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
		HashMap<String, String> params = ServletUtility.getRequestParameters(request);
		int size = Integer.parseInt(params.get(ParametersConfig.SIZE));
		int offset = Integer.parseInt(params.get(ParametersConfig.OFFSET));

		List<AnswerDto> answersDto;
		try {
			List<Answer> answers = m_daoManager.getAnswerDao().getQuestionAnswers(questionId, size, offset);
			answersDto = new ArrayList<AnswerDto>();
			for (Answer answer : answers) {
				try {
					AnswerDto answerDto = answer.toAnswerDto(user.getId());
					answersDto.add(answerDto);
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
		out.append(gson.toJson(answersDto));
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
		HashMap<String, String> params = ServletUtility.getRequestParameters(request);
		int size = Integer.parseInt(params.get(ParametersConfig.SIZE));
		int offset = Integer.parseInt(params.get(ParametersConfig.OFFSET));

		List<QuestionDto> questionsDto = new ArrayList<QuestionDto>();
		try {
			List<Question> newestQuestions = m_daoManager.getQuestionDao().getNewestQuestions(size, offset);
			for (Question question : newestQuestions) {
				try {
					QuestionDto questionDto = question.toQuestionDto(user.getId());
					questionsDto.add(questionDto);
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
		out.append(gson.toJson(questionsDto));
	}

	private void getBestQuestion(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
		HashMap<String, String> params = ServletUtility.getRequestParameters(request);
		int size = Integer.parseInt(params.get(ParametersConfig.SIZE));
		int offset = Integer.parseInt(params.get(ParametersConfig.OFFSET));

		List<QuestionDto> questionsDto = new ArrayList<QuestionDto>();
		List<Question> bestQuestions;
		try {
			bestQuestions = m_daoManager.getQuestionDao().getBestQuestions(size, offset);

			for (Question question : bestQuestions) {
				try {
					QuestionDto questionDto = question.toQuestionDto(user.getId());
					questionsDto.add(questionDto);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e1) {
			throw new ServletException(e1.getMessage());
		}

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		out.append(gson.toJson(questionsDto));
	}

}
