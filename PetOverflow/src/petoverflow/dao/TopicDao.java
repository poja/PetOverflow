package petoverflow.dao;

import java.util.List;

/**
 * The TopicDao interface provide a set of methods that show the connections
 * between topics and questions. All methods throws Exception in failure.
 */
public interface TopicDao {

	/**
	 * Get all question of a topic
	 * 
	 * @param topic
	 *            the searched topic
	 * @return a list of all questions' ids that have the searched topic
	 * @throws Exception
	 *             if fail
	 */
	public List<Integer> getQuestionsByTopic(String topic) throws Exception;

	/**
	 * Get all topics of a specific question
	 * 
	 * @param questionId
	 *            the question's id
	 * @return a list of all topics the question is related to
	 * @throws Exception
	 *             if fail
	 */
	public List<String> getQuestionTopics(int questionId) throws Exception;

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
	public void setTopics(int questionId, List<String> topics) throws Exception;

	/**
	 * Get all the current topics
	 * 
	 * @return a list of all topics
	 * @throws Exception
	 *             if fail
	 */
	public List<String> getAllTopics() throws Exception;

}
