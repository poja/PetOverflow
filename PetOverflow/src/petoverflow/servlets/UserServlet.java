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
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import petoverflow.Utility;
import petoverflow.dao.items.Answer;
import petoverflow.dao.items.Question;
import petoverflow.dao.items.User;
import petoverflow.dto.AnswerDto;
import petoverflow.dto.QuestionDto;
import petoverflow.dto.UserDto;

/**
 * The UserServlet class is a servlet that provide a set of services to read and
 * create and search users in the system.
 */
public class UserServlet extends AuthenticatedHttpServlet {

	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * petoverflow.servlets.AuthenticatedHttpServlet#doPost(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Map this request to:
		// - /user
		Pattern p = Pattern.compile("/user");

		String path = ServletUtility.getPath(request);
		Matcher m = p.matcher(path);
		if (!m.find()) {
			throw new ServletException("Invalid URI");
		}

		HashMap<String, Object> params = ServletUtility.getRequestParameters(request);
		String username = params.get(ParametersConfig.USERNAME).toString().toLowerCase();
		String password = params.get(ParametersConfig.PASSWORD).toString();
		String nickname = params.get(ParametersConfig.NICKNAME).toString();
		String description = params.get(ParametersConfig.DESCRIPTION).toString();
		String photoUrl = params.get(ParametersConfig.PHOTO_URL).toString();
		String phoneNum = params.get(ParametersConfig.PHONE_NUM).toString();
		boolean wantsSms = (boolean) params.get(ParametersConfig.WANTS_SMS);

		Gson gson = new Gson();
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();

		User newUser = null;
		try {
			if (m_daoManager.getUserDao().exist(username)) {
				HashMap<String, String> ans = new HashMap<String, String>();
				ans.put("errorMessage", "Sorry, this username already exists. Please choose a different one.");
				out.append(gson.toJson(ans));
				return;
			} else {
				newUser = m_daoManager.getUserDao().createUser(username, password, nickname, description, photoUrl,
						phoneNum, wantsSms);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		session.setAttribute("username", username);
		session.setAttribute("password", password);
		session.setAttribute("userId", newUser.getId());
		response.setContentType("application/json");

		if (newUser != null) {
			try {
				out.append(gson.toJson(new UserDto(newUser)));
				if (wantsSms) {
					Utility.sendSms(phoneNum, "Welcome to PetOverflow, " + nickname + "!");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * petoverflow.servlets.AuthenticatedHttpServlet#doAuthenticatedGet(javax.
	 * servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
	 * petoverflow.dao.User)
	 */
	protected void doAuthenticatedGet(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
		// Map this request to:
		// - /user/<user#>
		// - /user/<user#>/answers
		// - /user/<user#>/questions
		// - /user/leaders
		Pattern p1 = Pattern.compile("/user/([0-9]*)");
		Pattern p2 = Pattern.compile("/user/([0-9]*)/answers");
		Pattern p3 = Pattern.compile("/user/([0-9]*)/questions");
		Pattern p4 = Pattern.compile("/user/leaders");

		String path = ServletUtility.getPath(request);
		Matcher m1 = p1.matcher(path);
		Matcher m2 = p2.matcher(path);
		Matcher m3 = p3.matcher(path);
		Matcher m4 = p4.matcher(path);
		if (m4.find()) {
			getLeaders(request, response, user);
		} else if (m3.find()) {
			int requestedUserId = Integer.parseInt(m3.group(1));
			getUserQuestions(request, response, user, requestedUserId);
		} else if (m2.find()) {
			int requestedUserId = Integer.parseInt(m2.group(1));
			getUserAnswers(request, response, user, requestedUserId);
		} else if (m1.find()) {
			int requestedUserId = Integer.parseInt(m1.group(1));
			getUser(request, response, user, requestedUserId);
		} else {
			throw new ServletException("Invalid URI");
		}
	}

	protected void doAuthenticatedPut(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
		// Map this request to:
		// - /user/me/description
		// - /user/me/wantsSms
		// - /user/me/phone
		// - /user/me/photo
		Pattern p1 = Pattern.compile("/user/me/description");
		Pattern p2 = Pattern.compile("/user/me/wantsSms");
		Pattern p3 = Pattern.compile("/user/me/phone");
		Pattern p4 = Pattern.compile("/user/me/photo");

		String path = ServletUtility.getPath(request);
		Matcher m1 = p1.matcher(path);
		Matcher m2 = p2.matcher(path);
		Matcher m3 = p3.matcher(path);
		Matcher m4 = p4.matcher(path);
		if (m1.find()) {
			putUserDescription(request, response, user);
		} else if (m2.find()) {
			putUserWantsSms(request, response, user);
		} else if (m3.find()) {
			putUserPhone(request, response, user);
		} else if (m4.find()) {
			putUserPhoto(request, response, user);
		} else {
			throw new ServletException("Invalid URI");
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
		HashMap<String, Object> params = ServletUtility.getRequestParameters(request);
		int size = (((Double) params.get(ParametersConfig.SIZE))).intValue();
		int offset = ((Double) (params.get(ParametersConfig.OFFSET))).intValue();

		List<UserDto> leadersDto;
		try {
			List<User> leaders = m_daoManager.getUserDao().getMostRatedUsers(size, offset);
			leadersDto = UserDto.listToDto(leaders);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServletException(e1.getMessage());
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
		UserDto userDto;
		try {
			User requestedUser = m_daoManager.getUserDao().getUser(requestedUserId);
			userDto = new UserDto(requestedUser);
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
		HashMap<String, Object> params = ServletUtility.getRequestParameters(request);
		int size = (((Double) params.get(ParametersConfig.SIZE))).intValue();
		int offset = ((Double) params.get(ParametersConfig.OFFSET)).intValue();

		List<Answer> answers;
		try {
			answers = m_daoManager.getAnswerDao().getAnswersByAuthor(requestedUserId, size, offset);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServletException(e1.getMessage());
		}
		Utility.sortByTimestamp(answers);
		List<Answer> releventAnswers = answers.subList(0, Math.min(size, answers.size()));
		List<AnswerDto> releventAnswersDto;
		try {
			releventAnswersDto = AnswerDto.listToDto(releventAnswers, user.getId());
		} catch (Exception e) {
			throw new ServletException(e.getCause());
		}

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		out.append(gson.toJson(releventAnswersDto));
	}

	private void getUserQuestions(HttpServletRequest request, HttpServletResponse response, User user,
			int requestedUserId) throws ServletException, IOException {
		HashMap<String, Object> params = ServletUtility.getRequestParameters(request);
		int size = (((Double) params.get(ParametersConfig.SIZE))).intValue();
		int offset = ((Double) (params.get(ParametersConfig.OFFSET))).intValue();

		List<Question> questions;
		try {
			questions = m_daoManager.getQuestionDao().getQuestionsByAuthor(requestedUserId, size, offset);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServletException(e1.getMessage());
		}
		Utility.sortByTimestamp(questions);
		Utility.cutList(questions, size, offset);
		List<QuestionDto> releventAnswersDto;
		try {
			releventAnswersDto = QuestionDto.listToDto(questions, user.getId());
		} catch (Exception e) {
			throw new ServletException(e.getCause());
		}

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		out.append(gson.toJson(releventAnswersDto));
	}

	private void putUserDescription(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
		HashMap<String, Object> params = ServletUtility.getRequestParameters(request);
		String description = ((String) params.get(ParametersConfig.DESCRIPTION));
		try {
			user.setDescription(description);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	private void putUserWantsSms(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
		HashMap<String, Object> params = ServletUtility.getRequestParameters(request);
		Boolean wantsSms = ((Boolean) params.get(ParametersConfig.WANTS_SMS));
		try {
			user.setWantsSms(wantsSms);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	private void putUserPhone(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
		HashMap<String, Object> params = ServletUtility.getRequestParameters(request);
		String phoneNumber = ((String) params.get(ParametersConfig.PHONE_NUM));
		try {
			user.setPhoneNum(phoneNumber);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	private void putUserPhoto(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
		HashMap<String, Object> params = ServletUtility.getRequestParameters(request);
		String photoUrl = ((String) params.get(ParametersConfig.PHOTO_URL));
		try {
			user.setPhoto(photoUrl);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

}
