package petoverflow.dao;

import java.sql.Timestamp;
import java.util.List;

import petoverflow.dao.items.Answer;
import petoverflow.dao.items.Question;
import petoverflow.dao.items.User;

/**
 * The AnswerDao interface provide a set of methods to get data about answers to
 * related questions. All methods throws Exception in failure.
 */
public interface AnswerDao {

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
	 * Create a new answer
	 * 
	 * @param text
	 *            text of the answer
	 * @param authorId
	 *            id of the author
	 * @param questionId
	 *            the id of the question the answer is related to
	 * @return the new answer
	 * @throws Exception
	 *             if fail
	 */
	public Answer createAnswer(String text, int authorId, int questionId) throws Exception;

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
	public User getAnswerAuthor(int answerId) throws Exception;

	/**
	 * Get the id of the question this answer is related to
	 * 
	 * @param answerId
	 *            the answer id
	 * @return id of the question of this answer context
	 * @throws Exception
	 *             if fail
	 */
	public Question getAnswerQuestion(int answerId) throws Exception;

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
	 * Get answers by specific user in range [offset, offset + size)
	 * 
	 * @param authorId
	 *            the user id
	 * @param size
	 *            the wanted size of the list
	 * @param offset
	 *            the wanted offset of the total list
	 * @return a sub list of the list of the answers the user published
	 * @throws Exception
	 *             if fail
	 */
	public List<Answer> getAnswersByAuthor(int authorId, int size, int offset) throws Exception;

	/**
	 * Get all answers by specific user
	 * 
	 * @param authorId
	 *            the user id
	 * @return a list with all answers by the user
	 * @throws Exception
	 *             if fail
	 */
	public List<Answer> getAnswersByAuthorAll(int authorId) throws Exception;

	/**
	 * Get answers to a specific question in range [offset, offset + size)
	 * 
	 * @param questionId
	 *            the question id
	 * @param size
	 *            the wanted size of the list
	 * @param offset
	 *            the wanted offset of the total list
	 * @return a sub list of the list of all answers to the question
	 * @throws Exception
	 *             if fail
	 */
	public List<Answer> getQuestionAnswers(int questionId, int size, int offset) throws Exception;

	/**
	 * Get all answers to a specific question
	 * 
	 * @param questionId
	 *            the question id
	 * @return a list with all answers to the question
	 * @throws Exception
	 *             if fail
	 */
	public List<Answer> getQuestionAnswersAll(int questionId) throws Exception;

}
