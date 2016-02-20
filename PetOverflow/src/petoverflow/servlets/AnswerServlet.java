package petoverflow.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import petoverflow.dao.items.Answer;
import petoverflow.dao.items.User;
import petoverflow.dao.items.Vote;
import petoverflow.dao.items.Vote.VoteType;
import petoverflow.dto.AnswerDto;

/**
 * The AnswerServlet class is a servlet that provide a set of services to
 * create, read and search answers to questions by users.
 */
public class AnswerServlet extends AuthenticatedHttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * Do Authenticated Put
	 * 
	 * Authenticated version of {@link HttpServlet#doPut}
	 */
	protected void doAuthenticatedPut(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
		// Map this request to:
		// - /answer/<answer#>/vote
		Pattern p = Pattern.compile("/answer/([0-9]*)/vote");

		String path = ServletUtility.getPath(request);
		Matcher m = p.matcher(path);
		if (!m.find()) {
			throw new ServletException("Invalid URI");
		}
		int answerId = Integer.parseInt(m.group(1));

		HashMap<String, Object> params = ServletUtility.getRequestParameters(request);
		String voteType = Integer.toString(((Double) params.get(ParametersConfig.VOTE_TYPE)).intValue());

		// TODO check own vote
		try {
			if (voteType.equals(ParametersConfig.VOTE_TYPE_NONE)) {
				m_daoManager.getAnswerVoteDao().removeVote(answerId, user.getId());
			} else if (voteType.equals(ParametersConfig.VOTE_TYPE_UP)) {
				m_daoManager.getAnswerVoteDao().addVote(answerId, new Vote(user.getId(), VoteType.Up));
			} else if (voteType.equals(ParametersConfig.VOTE_TYPE_DOWN)) {
				m_daoManager.getAnswerVoteDao().addVote(answerId, new Vote(user.getId(), VoteType.Down));
			} else {
				throw new ServletException("The vote is not one of the required types.");
			}
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
		Pattern p = Pattern.compile("/answer");
		String path = ServletUtility.getPath(request);
		Matcher m = p.matcher(path);
		if (!m.find()) {
			throw new ServletException("Invalid URI");
		}

		HashMap<String, Object> params = ServletUtility.getRequestParameters(request);
		String text = params.get(ParametersConfig.TEXT).toString();
		int questionId = ((Double) params.get(ParametersConfig.QUESTION_ID)).intValue();

		try {
			m_daoManager.getAnswerDao().createAnswer(text, user.getId(), questionId);
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
		Pattern p = Pattern.compile("/answer/([0-9]*)");

		String path = ServletUtility.getPath(request);
		Matcher m = p.matcher(path);
		if (!m.find()) {
			throw new ServletException("Invalid URI");
		}
		int answerId = Integer.parseInt(m.group(1));

		AnswerDto answerDto;
		try {
			Answer answer = m_daoManager.getAnswerDao().getAnswer(answerId);
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
