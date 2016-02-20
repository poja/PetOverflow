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
		// - /topic/<#topic>/questions
		Pattern p1 = Pattern.compile("/topic/popular");
		Pattern p2 = Pattern.compile("/topic/([[a-z][A-Z]]*)/questions");

		String path = ServletUtility.getPath(request);
		Matcher m1 = p1.matcher(path);
		Matcher m2 = p2.matcher(path);
		if (m1.find()) {
			getPopularTopics(request, response, user);
		} else if (m2.find()) {
			String topic = m2.group(1);
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

		List<Topic> topics;
		try {
			topics = m_daoManager.getTopicDao().getPopularTopics(size, offset);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		}
		List<TopicDto> topicsDto = new ArrayList<TopicDto>();
		for (Topic topic : topics) {
			try {
				TopicDto topicDto = topic.toTopicDto();
				topicsDto.add(topicDto);
			} catch (Exception e) {
				e.printStackTrace();
			}
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

		List<Question> bestQuestions;
		try {
			bestQuestions = m_daoManager.getTopicDao().getBestQuestionsByTopic(topic, size, offset);
		} catch (Exception e) {
			throw new ServletException(e.getMessage());
		}

		List<QuestionDto> questionsDto = new ArrayList<QuestionDto>();
		for (Question question : bestQuestions) {
			try {
				QuestionDto questionDto = question.toQuestionDto(user.getId());
				questionsDto.add(questionDto);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		out.append(gson.toJson(questionsDto));
	}

}
