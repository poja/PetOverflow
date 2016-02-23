package petoverflow.dao;

import java.sql.Timestamp;
import java.util.List;

import petoverflow.dao.items.Question;
import petoverflow.dao.items.User;

/**
 * The QuestionDao interface provide a set of methods that allow reading and
 * creating questions. All methods throws Exception in failure.
 */
public interface QuestionDao {

	/**
	 * Checks if a question exist
	 * 
	 * @param questionId
	 *            the question's id
	 * @return true if the question exist, else - false
	 * @throws Exception
	 *             if fail
	 */
	public boolean exist(int questionId) throws Exception;

	/**
	 * Create a new question
	 * 
	 * @param text
	 *            text of the question
	 * @param authorId
	 *            id of the author
	 * @param topics
	 *            a list of the topics for the questions
	 * @return the new question
	 * @throws Exception
	 *             if fail
	 */
	public Question createQuestion(String text, int authorId, List<String> topics) throws Exception;

	/**
	 * Get a question by it's id
	 * 
	 * @param questionId
	 *            the question's id
	 * @return the requested question if exist
	 * @throws Exception
	 *             if fail
	 */
	public Question getQuestion(int questionId) throws Exception;

	/**
	 * Get the text of a question
	 * 
	 * @param questionId
	 *            the question's id
	 * @return the text of the question
	 * @throws Exception
	 *             if fail
	 */
	public String getQuestionText(int questionId) throws Exception;

	/**
	 * Get the id of a question's author
	 * 
	 * @param questionId
	 *            the question's id
	 * @return the id of the question's author
	 * @throws Exception
	 *             if fail
	 */
	public User getQuestionAuthor(int questionId) throws Exception;

	/**
	 * Get the time stamp of a question
	 * 
	 * @param questionId
	 *            the question's id
	 * @return the time stamp the question submitted
	 * @throws Exception
	 *             if fail
	 */
	public Timestamp getQuestionTimestamp(int questionId) throws Exception;

	/**
	 * Get questions by a specific user in range [offset, offset + size)
	 * 
	 * @param authorId
	 *            the user's id
	 * @param size
	 *            the wanted size of the list
	 * @param offset
	 *            the wanted offset of the total list
	 * @return a sub list of the list of all questions published by the user
	 * @throws Exception
	 *             if fail
	 */
	public List<Question> getQuestionsByAuthor(int authorId, int size, int offset) throws Exception;

	/**
	 * Get all questions by a specific user
	 * 
	 * @param authorId
	 *            the user's id
	 * @return a list of all questions published by the user
	 * @throws Exception
	 *             if fail
	 */
	public List<Question> getQuestionsByAuthorAll(int authorId) throws Exception;

	/**
	 * Get the newest questions list
	 * 
	 * @param size
	 *            the wanted size of the list
	 * @param offset
	 *            the wanted offset of the total list
	 * @return a list of the newest questions
	 * @throws Exception
	 *             if fail
	 */
	public List<Question> getNewestQuestions(int size, int offset) throws Exception;

	/**
	 * Get the best questions list
	 * 
	 * @param size
	 *            the wanted size of the list
	 * @param offset
	 *            the wanted offset of the total list
	 * @return a list of the best questions
	 * @throws Exception
	 *             if fail
	 */
	public List<Question> getBestQuestions(int size, int offset) throws Exception;

}
