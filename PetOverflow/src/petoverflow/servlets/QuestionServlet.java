package petoverflow.servlets;

import java.io.IOException;
import java.io.PrintWriter;
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

		HashMap<String, Object> params = ServletUtility.getRequestParameters(request);
		String voteType = Integer.toString(((Double) params.get(ParametersConfig.VOTE_TYPE)).intValue());

		try {
			if (voteType.equals(ParametersConfig.VOTE_TYPE_NONE)) {
				m_daoManager.getQuestionVoteDao().removeVote(questionId, user.getId());
			} else if (voteType.equals(ParametersConfig.VOTE_TYPE_UP)) {
				m_daoManager.getQuestionVoteDao().addVote(questionId, new Vote(user.getId(), VoteType.Up));
			} else if (voteType.equals(ParametersConfig.VOTE_TYPE_DOWN)) {
				m_daoManager.getQuestionVoteDao().addVote(questionId, new Vote(user.getId(), VoteType.Down));
			} else {
				throw new ServletException("The vote is not one of the required types.");
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

		HashMap<String, Object> params = ServletUtility.getRequestParameters(request);
		String text = (String) params.get(ParametersConfig.TEXT);

		List<String> topics;
		try {
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) params.get(ParametersConfig.TOPICS);
			topics = list;
		} catch (ClassCastException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

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
		// - /question/search
		// - /question/<question#>
		// - /question/<question#>/answers
		Pattern p1 = Pattern.compile("/question/best");
		Pattern p2 = Pattern.compile("/question/newest");
		Pattern p3 = Pattern.compile("/question/search");
		Pattern p4 = Pattern.compile("/question/([0-9]+)");
		Pattern p5 = Pattern.compile("/question/([0-9]+)/answers");

		String path = ServletUtility.getPath(request);
		Matcher m1 = p1.matcher(path);
		Matcher m2 = p2.matcher(path);
		Matcher m3 = p3.matcher(path);
		Matcher m4 = p4.matcher(path);
		Matcher m5 = p5.matcher(path);

		if (m5.find()) {
			int questionId = Integer.parseInt(m5.group(1));
			getBestAnswers(request, response, user, questionId);
		} else if (m4.find()) {
			int questionId = Integer.parseInt(m4.group(1));
			getAQuestion(request, response, user, questionId);
		} else if (m3.find()) {
			searchQuestion(request, response, user);
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
			questionDto = new QuestionDto(question, user.getId());
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
		HashMap<String, Object> params = ServletUtility.getRequestParameters(request);
		int size = ((Double) params.get(ParametersConfig.SIZE)).intValue();
		int offset = ((Double) params.get(ParametersConfig.OFFSET)).intValue();

		List<AnswerDto> answersDto;
		try {
			List<Answer> answers = m_daoManager.getAnswerDao().getQuestionAnswers(questionId, size, offset);
			answersDto = AnswerDto.listToDto(answers, user.getId());
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
		HashMap<String, Object> params = ServletUtility.getRequestParameters(request);
		int size = ((Double) params.get(ParametersConfig.SIZE)).intValue();
		int offset = ((Double) params.get(ParametersConfig.OFFSET)).intValue();

		List<QuestionDto> questionsDto;
		try {
			List<Question> newestQuestions = m_daoManager.getQuestionDao().getNewestQuestions(size, offset);
			questionsDto = QuestionDto.listToDto(newestQuestions, user.getId());
		} catch (Exception e) {
			throw new ServletException(e);
		}

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		out.append(gson.toJson(questionsDto));
	}

	private void getBestQuestion(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
		HashMap<String, Object> params = ServletUtility.getRequestParameters(request);
		int size = ((Double) params.get(ParametersConfig.SIZE)).intValue();
		int offset = ((Double) params.get(ParametersConfig.OFFSET)).intValue();

		List<QuestionDto> questionsDto;
		try {
			List<Question> bestQuestions = m_daoManager.getQuestionDao().getBestQuestions(size, offset);
			questionsDto = QuestionDto.listToDto(bestQuestions, user.getId());
		} catch (Exception e1) {
			throw new ServletException(e1.getMessage());
		}

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		out.append(gson.toJson(questionsDto));
	}

	private void searchQuestion(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
		HashMap<String, Object> params = ServletUtility.getRequestParameters(request);
		String text = (String) params.get(ParametersConfig.TEXT);
		int size = ((Double) params.get(ParametersConfig.SIZE)).intValue();
		int offset = ((Double) params.get(ParametersConfig.OFFSET)).intValue();

		List<QuestionDto> questionsDto;
		try {
			List<Question> searchedQuestions = m_daoManager.getQuestionDao().searchQuestion(text, size, offset);
			questionsDto = QuestionDto.listToDto(searchedQuestions, user.getId());
		} catch (Exception e) {
			throw new ServletException(e);
		}

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		out.append(gson.toJson(questionsDto));
	}

}
