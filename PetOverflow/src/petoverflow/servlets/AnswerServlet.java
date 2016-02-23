package petoverflow.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import petoverflow.Utility;
import petoverflow.dao.items.Answer;
import petoverflow.dao.items.Question;
import petoverflow.dao.items.Topic;
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
			Answer ans = m_daoManager.getAnswerDao().createAnswer(text, user.getId(), questionId);
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			Gson gson = new Gson();
			out.append(gson.toJson(ans.toAnswerDto(user.getId())));
			
			notifyAsker(questionId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		}
	}

	/**
	 * Check whether the asker of the question requested notifications, and if
	 * so, send them the notifications.
	 * 
	 * @param questionId
	 *            The id of the question that was answered
	 */
	private void notifyAsker(int questionId) throws Exception {
		Question q = m_daoManager.getQuestionDao().getQuestion(questionId);
		User asker = q.getAuthor();
		List<Topic> topics = q.getTopics();

		if (asker.getWantsSms()) {
			String message = "Your question on PetOverflow";
			if (topics.size() > 0) {
				message += ", about '" + topics.get(0).getName() + "',";
			}
			message += " has been answered. Go check it out!";
			Utility.sendSms(asker.getPhoneNum(), message);
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
