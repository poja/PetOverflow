package petoverflow.dao;

import java.sql.Timestamp;
import java.util.List;

/**
 * The AnswerDao interface provide a set of methods to get data about answers to
 * related questions. All methods throws Exception in failure.
 */
public interface AnswerDao {

	/**
	 * Get an answer by it's id
	 * 
	 * @param answerId
	 *            the answer's id
	 * @return the requested answer
	 * @throws Exception
	 *             if fail
	 */
	public Answer getAnswer(int answerId) throws Exception;

	/**
	 * Check if an answer is exist
	 * 
	 * @param answerId
	 *            id of the searched answer
	 * @return true if exist an answer with the id, else -false
	 * @throws Exception
	 *             if fail
	 */
	public boolean exist(int answerId) throws Exception;

	/**
	 * Get a text of an answer
	 * 
	 * @param answerId
	 *            the answer id
	 * @return the text of the answer
	 * @throws Exception
	 *             if fail
	 */
	public String getAnswerText(int answerId) throws Exception;

	/**
	 * Get the id of the answer's author
	 * 
	 * @param answerId
	 *            the answer id
	 * @return id of the user that published the answer
	 * @throws Exception
	 *             if fail
	 */
	public int getAnswerAuthorId(int answerId) throws Exception;

	/**
	 * Get the id of the question this answer is related to
	 * 
	 * @param answerId
	 *            the answer id
	 * @return id of the question of this answer context
	 * @throws Exception
	 *             if fail
	 */
	public int getAnswerQuestionId(int answerId) throws Exception;

	/**
	 * Get the time stamp of this answer
	 * 
	 * @param answerId
	 *            the answer id
	 * @return the time the answer was published
	 * @throws Exception
	 *             if fail
	 */
	public Timestamp getAnswerTimestamp(int answerId) throws Exception;

	/**
	 * Get all answers by specific user
	 * 
	 * @param authorId
	 *            the user id
	 * @return a list of the answer the user published
	 * @throws Exception
	 *             if fail
	 */
	public List<Answer> getAnswersByAuthor(int authorId) throws Exception;

	/**
	 * Get all answers to a specific question
	 * 
	 * @param questionId
	 *            the question id
	 * @return a list of all answers to the question
	 * @throws Exception
	 *             if fail
	 */
	public List<Answer> getQuestionAnswers(int questionId) throws Exception;

}
