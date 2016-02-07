package petoverflow.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import petoverflow.authentication.AuthenticatedHttpServlet;
import petoverflow.dao.Question;
import petoverflow.dao.QuestionDao;
import petoverflow.dao.TopicDao;
import petoverflow.dao.User;
import petoverflow.dao.derby.QuestionDaoDerby;
import petoverflow.dao.derby.TopicDaoDerby;

/**
 * The TopicServlet class is a servlet that provide a set of services to read
 * and search in all topics.
 */
public class TopicServlet extends AuthenticatedHttpServlet {

	private QuestionDao m_questionDao;

	private TopicDao m_topicDao;

	public TopicServlet() {
		super();
		m_questionDao = QuestionDaoDerby.getInstance();
		m_topicDao = TopicDaoDerby.getInstance();
	}

	private static final long serialVersionUID = 1L;

	protected void doAuthenticatedGet(HttpServletRequest request, HttpServletResponse response, User user)
			throws ServletException, IOException {
		// Map this request to:
		// - /topic/popular
		// - /topic/<#topic>/questions

		String path = request.getPathInfo();
		int index = path.indexOf('/');
		if (index != 0) {
			throw new ServletException("Invalid URI");
		}
		path = path.substring(index + 1);
		if (!path.substring(0, index).equals("topic")) {
			throw new ServletException("Invalid URI");
		}
		index = path.indexOf('/');
		path = path.substring(index + 1);
		if (path.equals("popular")) {
			getPopularTopics(request, response, user);
		} else {
			index = path.indexOf('/');
			if (!path.substring(index + 1).equals("questions")) {
				throw new ServletException("Invalid URI");
			}
			String topic = path.substring(0, index);
			getBestQuestionInTopics(request, response, user, topic);
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
		int size = 0;
		int offset = 0;
		// TODO get parameters

		List<String> topics;
		try {
			topics = m_topicDao.getAllTopics();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		}
		final HashMap<String, Double> topicsRating = new HashMap<String, Double>();
		for (String topic : topics) {
			double score;
			try {
				score = getTopicScore(topic);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServletException(e.getMessage());
			}
			topicsRating.put(topic, score);
		}
		Collections.sort(topics, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return (int) (topicsRating.get(o1) - topicsRating.get(o2));
			}
		});

		if (offset >= topics.size()) {
			topics = new ArrayList<String>();
		} else {
			topics = topics.subList(offset, Math.min(topics.size() + 1, offset + size));
		}

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		out.append(gson.toJson(topics));
	}

	/**
	 * Get the best question in a specific topic
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
	private void getBestQuestionInTopics(HttpServletRequest request, HttpServletResponse response, User user,
			String topic) throws ServletException, IOException {
		int size = 0;
		int offset = 0;
		// TODO get parameters

		List<Integer> questionIds;
		try {
			questionIds = m_topicDao.getQuestionsByTopic(topic);
		} catch (Exception e2) {
			e2.printStackTrace();
			throw new ServletException(e2.getMessage());
		}
		List<Question> questions = new ArrayList<Question>();
		final HashMap<Integer, Double> questionRating = new HashMap<Integer, Double>();
		for (Integer questionId : questionIds) {
			Question question;
			try {
				question = m_questionDao.getQuestion(questionId);
			} catch (Exception e1) {
				e1.printStackTrace();
				throw new ServletException(e1.getMessage());
			}
			questions.add(question);
			try {
				questionRating.put(questionId, question.getRating());
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServletException(e.getMessage());
			}
		}
		Collections.sort(questions, new Comparator<Question>() {

			@Override
			public int compare(Question o1, Question o2) {
				double r1 = questionRating.get(o1.getId());
				double r2 = questionRating.get(o2.getId());
				if (r1 > r2) {
					return 1;
				} else if (r1 < r2) {
					return -1;
				} else {
					return 0;
				}
			}
		});

		if (offset >= questions.size()) {
			questions = new ArrayList<Question>();
		} else {
			questions = questions.subList(offset, Math.min(questions.size() + 1, offset + size));
		}

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		out.append(gson.toJson(questions));
	}

	/**
	 * Get a score of a topic
	 * 
	 * @param topic
	 *            the topic name
	 * @return score of the topic
	 * @throws Exception
	 *             if DAO fail
	 */
	private double getTopicScore(String topic) throws Exception {
		List<Integer> questionIds = m_topicDao.getQuestionsByTopic(topic);
		double rating = 0;
		for (Integer questionId : questionIds) {
			Question question = m_questionDao.getQuestion(questionId);
			rating += question.getRating();
		}
		return rating;
	}

}
