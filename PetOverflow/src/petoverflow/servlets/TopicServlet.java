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

import petoverflow.dao.items.Question;
import petoverflow.dao.items.Topic;
import petoverflow.dao.items.User;
import petoverflow.dto.QuestionDto;
import petoverflow.dto.TopicDto;

/**
 * The TopicServlet class is a servlet that provide a set of services to read
 * and search in all topics.
 */
public class TopicServlet extends AuthenticatedHttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doAuthenticatedGet(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
		// Map this request to:
		// - /topic/popular
		// - /topic/search
		// - /topic/<#topic>/questions
		Pattern p1 = Pattern.compile("/topic/popular");
		Pattern p2 = Pattern.compile("/topic/search");
		Pattern p3 = Pattern.compile("/topic/([[a-z][A-Z]]*)/questions");

		String path = ServletUtility.getPath(request);
		Matcher m1 = p1.matcher(path);
		Matcher m2 = p2.matcher(path);
		Matcher m3 = p3.matcher(path);
		if (m1.find()) {
			getPopularTopics(request, response, user);
		} else if (m2.find()) {
			searchTopics(request, response, user);
		} else if (m3.find()) {
			String topic = m3.group(1);
			getBestQuestionInTopic(request, response, user, topic);
		} else {
			throw new ServletException("Invalid URI");
		}
	}

	/**
	 * Get a list of popular topics
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
	private void getPopularTopics(HttpServletRequest request, HttpServletResponse response, User user)
			throws IOException, ServletException {
		HashMap<String, Object> params = ServletUtility.getRequestParameters(request);
		int size = (((Double) params.get(ParametersConfig.SIZE))).intValue();
		int offset = ((Double) (params.get(ParametersConfig.OFFSET))).intValue();

		List<TopicDto> topicsDto;
		try {
			List<Topic> topics = m_daoManager.getTopicDao().getPopularTopics(size, offset);
			topicsDto = TopicDto.listToDto(topics, user.getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		}

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		out.append(gson.toJson(topicsDto));
	}

	private void searchTopics(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
		HashMap<String, Object> params = ServletUtility.getRequestParameters(request);
		String text = (String) params.get(ParametersConfig.TEXT);
		int size = ((Double) params.get(ParametersConfig.SIZE)).intValue();
		int offset = ((Double) params.get(ParametersConfig.OFFSET)).intValue();

		List<TopicDto> topicsDto;
		try {
			List<Topic> searchedTopics = m_daoManager.getTopicDao().searchTopics(text, size, offset);
			topicsDto = TopicDto.listToDto(searchedTopics, user.getId());
		} catch (Exception e) {
			throw new ServletException(e);
		}

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		out.append(gson.toJson(topicsDto));
	}

	/**
	 * Get the best questions in a specific topic
	 * 
	 * @param request
	 *            the request from the client
	 * @param response
	 *            the response object
	 * @param user
	 *            the current logged in user
	 * @param topic
	 *            the topic name
	 * @throws ServletException
	 *             if DAO fail
	 * @throws IOException
	 *             if fail to write to client
	 */
	private void getBestQuestionInTopic(HttpServletRequest request, HttpServletResponse response, User user,
			String topic) throws ServletException, IOException {
		HashMap<String, Object> params = ServletUtility.getRequestParameters(request);
		int size = (((Double) params.get(ParametersConfig.SIZE))).intValue();
		int offset = ((Double) (params.get(ParametersConfig.OFFSET))).intValue();

		List<QuestionDto> questionsDto;
		try {
			List<Question> bestQuestions = m_daoManager.getTopicDao().getBestQuestionsByTopic(topic, size, offset);
			questionsDto = QuestionDto.listToDto(bestQuestions, user.getId());
		} catch (Exception e) {
			throw new ServletException(e.getMessage());
		}

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		out.append(gson.toJson(questionsDto));
	}

}
