package petoverflow.dao;

import java.util.List;

import petoverflow.dao.items.Question;
import petoverflow.dao.items.Topic;

/**
 * The TopicDao interface provide a set of methods that show the connections
 * between topics and questions. All methods throws Exception in failure.
 */
public interface TopicDao {

	/**
	 * Get a rating of a topic
	 * 
	 * @param topicName
	 *            the topic name
	 * @return the topic's rating
	 * @throws Exception
	 *             if fail
	 */
	public double getTopicRating(String topicName) throws Exception;

	/**
	 * Get all topics of a specific question
	 * 
	 * @param questionId
	 *            the question's id
	 * @return a list of all topics the question is related to
	 * @throws Exception
	 *             if fail
	 */
	public List<Topic> getQuestionTopics(int questionId) throws Exception;

	/**
	 * Set topics to a question
	 * 
	 * @param questionId
	 *            the question's id
	 * @param topics
	 *            a list of all question's topics
	 * @throws Exception
	 *             if fail
	 */
	public void setQuestionTopics(int questionId, List<String> topics) throws Exception;

	/**
	 * Get all the current topics
	 * 
	 * @return a list of all topics
	 * @throws Exception
	 *             if fail
	 */
	public List<Topic> getAllTopics() throws Exception;

	/**
	 * Get questions of a topic in range [offset, offset + size)
	 * 
	 * @param topic
	 *            the searched topic
	 * @return a sub list of the list of all questions that have the searched
	 *         topic
	 * @throws Exception
	 *             if fail
	 */
	public List<Question> getQuestionsByTopic(String topic, int size, int offset) throws Exception;

	/**
	 * Get all questions of a topic
	 * 
	 * @param topic
	 *            the searched topic name
	 * @return a list of all questions that have the search topic
	 * @throws Exception
	 *             if fail
	 */
	public List<Question> getQuestionsByTopicAll(String topic) throws Exception;

	/**
	 * Get list of popular topics in range [offset, offset + size)
	 * 
	 * @param size
	 *            the wanted size
	 * @param offset
	 *            the wanted offset
	 * @return a sub list of the popular topics list
	 * @throws Exception
	 *             if fail
	 */
	public List<Topic> getPopularTopics(int size, int offset) throws Exception;

	/**
	 * Get a list of the best questions in a topic in range [offset, offset +
	 * size)
	 * 
	 * @param topic
	 *            the topic name
	 * @param size
	 *            the wanted size
	 * @param offset
	 *            the wanted offset
	 * @return a sub list of the list of all best questions of the topic
	 * @throws Exception
	 *             if fail
	 */
	public List<Question> getBestQuestionsByTopic(String topic, int size, int offset) throws Exception;

	/**
	 * Find topics with the given text
	 * 
	 * @param text To search inside topics
	 * @param size The number of topics to return
	 * @param offset An offset, if the first ones are not needed
	 * @return Topics that have the given text, maximum `size` topics, with the optional offset 
	 * @throws Exception
	 */
	public List<Topic> searchTopics(String text, int size, int offset) throws Exception;

}
