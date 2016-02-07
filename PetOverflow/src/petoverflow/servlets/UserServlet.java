package petoverflow.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import petoverflow.dao.User;
import petoverflow.dao.UserDao;
import petoverflow.dao.derby.AnswerDaoDerby;
import petoverflow.dao.derby.QuestionDaoDerby;
import petoverflow.dao.derby.UserDaoDerby;
import petoverflow.dto.AnswerDto;
import petoverflow.dto.QuestionDto;
import petoverflow.dto.UserDto;

/**
 * The UserServlet class is a servlet that provide a set of services to read and
 * create and search users in the system.
 */
public class UserServlet extends AuthenticatedHttpServlet {

	private UserDao m_userDao;

	private AnswerDao m_answerDao;

	private QuestionDao m_questionDao;

	private static final long serialVersionUID = 1L;

	public UserServlet() {
		super();
		m_userDao = UserDaoDerby.getInstance();
		m_answerDao = AnswerDaoDerby.getInstance();
		m_questionDao = QuestionDaoDerby.getInstance();
	}

	protected void doAuthenticatedGet(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
		// Map this request to:
		// - /user/<user#>
		// - /user/<user#>/answers
		// - /user/<user#>/questions
		// - /user/leaders

		String path = request.getPathInfo();
		int index = path.indexOf('/');
		if (index != 0) {
			throw new ServletException("Invalid URI");
		}
		path = path.substring(index + 1);
		index = path.indexOf('/');
		if (!path.substring(0, index).equals("user")) {
			throw new ServletException("Invalid URI");
		}
		path = path.substring(index + 1);
		index = path.indexOf('/');
		if (index < 0) {
			if (path.equals("leaders")) { // /user/leaders
				getLeaders(request, response, user);
			} else { // /user/<user#>
				int requestedUserId;
				try {
					requestedUserId = Integer.parseInt(path);
				} catch (NumberFormatException e) {
					throw new ServletException("Invalid URI");
				}
				getUser(request, response, user, requestedUserId);
			}
		} else {
			int requestedUserId;
			try {
				requestedUserId = Integer.parseInt(path.substring(0, index));
			} catch (NumberFormatException e) {
				throw new ServletException("Invalid URI");
			}
			path = path.substring(index + 1);
			if (path.equals("answers")) { // /user/<user#>/answers
				getUserAnswers(request, response, user, requestedUserId);
			} else if (path.equals("questions")) { // /user/<user#>/questions
				getUserQuestions(request, response, user, requestedUserId);
			} else {
				throw new ServletException("Invalid URI");
			}
		}
	}

	/**
	 * Get a list of the leaders users by their rating
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
	private void getLeaders(HttpServletRequest request, HttpServletResponse response, User user)
			throws IOException, ServletException {
		int size = 0;
		int offset = 0;
		// TODO parameters
		List<User> leaders;
		try {
			leaders = m_userDao.getMostRatedUsers(size, offset);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServletException(e1.getMessage());
		}
		List<UserDto> leadersDto = new ArrayList<UserDto>();
		for (User leader : leaders) {
			UserDto leaderDto;
			try {
				leaderDto = leader.toUserDto();
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServletException(e.getMessage());
			}
			leadersDto.add(leaderDto);
		}
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		out.append(gson.toJson(leadersDto));
	}

	/**
	 * Get the data about a user
	 * 
	 * @param request
	 *            the request from the client
	 * @param response
	 *            the response object
	 * @param user
	 *            the current logged in user
	 * @param requestedUserId
	 *            the id of the requested user
	 * @throws ServletException
	 *             if DAO fail
	 * @throws IOException
	 *             if fail to write to client
	 */
	private void getUser(HttpServletRequest request, HttpServletResponse response, User user, int requestedUserId)
			throws IOException, ServletException {
		User requestedUser;
		UserDto userDto;
		try {
			requestedUser = m_userDao.getUser(requestedUserId);
			userDto = requestedUser.toUserDto();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		}

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		out.append(gson.toJson(userDto));
	}

	/**
	 * Get the latest answers of a user
	 * 
	 * @param request
	 *            the request from the client
	 * @param response
	 *            the response object
	 * @param user
	 *            the current logged in user
	 * @param requestedUserId
	 *            the id of the requested user
	 * @throws ServletException
	 *             if DAO fail
	 * @throws IOException
	 *             if fail to write to client
	 */
	private void getUserAnswers(HttpServletRequest request, HttpServletResponse response, User user,
			int requestedUserId) throws IOException, ServletException {
		int size = 0; // TODO get parameter
		List<Answer> answers;
		try {
			answers = m_answerDao.getAnswersByAuthor(requestedUserId);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServletException(e1.getMessage());
		}
		Collections.sort(answers, new Comparator<Answer>() {

			@Override
			public int compare(Answer o1, Answer o2) {
				try {
					Timestamp t1 = o1.getTimestamp();
					Timestamp t2 = o2.getTimestamp();
					if (t1.equals(t2)) {
						return 0;
					} else if (t1.after(t2)) {
						return -1;
					} else
						return 1;

				} catch (Exception e) {
					return 0;
				}
			}
		});
		List<Answer> releventAnswers = answers.subList(0, Math.min(size, answers.size() + 1));
		List<AnswerDto> releventAnswersDto = new ArrayList<AnswerDto>();
		for (Answer answer : releventAnswers) {
			AnswerDto answerDto;
			try {
				answerDto = answer.toAnswerDto(user.getId());
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServletException(e.getMessage());
			}
			releventAnswersDto.add(answerDto);
		}
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		out.append(gson.toJson(releventAnswersDto));
	}

	private void getUserQuestions(HttpServletRequest request, HttpServletResponse response, User user,
			int requestedUserId) throws ServletException, IOException {
		int size = 0; // TODO get parameter
		List<Question> questions;
		try {
			questions = m_questionDao.getQuestionsByAuthor(requestedUserId);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServletException(e1.getMessage());
		}
		Collections.sort(questions, new Comparator<Question>() {

			@Override
			public int compare(Question o1, Question o2) {
				try {
					Timestamp t1 = o1.getTimestamp();
					Timestamp t2 = o2.getTimestamp();
					if (t1.equals(t2)) {
						return 0;
					} else if (t1.after(t2)) {
						return -1;
					} else
						return 1;

				} catch (Exception e) {
					return 0;
				}
			}
		});
		List<Question> releventAnswers = questions.subList(0, Math.min(size, questions.size() + 1));
		List<QuestionDto> releventAnswersDto = new ArrayList<QuestionDto>();
		for (Question question : releventAnswers) {
			QuestionDto questionDto;
			try {
				questionDto = question.toQuestionDto(user.getId());
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServletException(e.getMessage());
			}
			releventAnswersDto.add(questionDto);
		}
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		out.append(gson.toJson(releventAnswersDto));
	}

}
