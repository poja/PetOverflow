package petoverflow.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * The QuestionDao interface provide a set of methods that allow reading and
 * creating questions. All methods throws Exception in failure.
 */
public interface QuestionDao {

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
	 * Get all question by a specific user
	 * 
	 * @param authorId
	 *            the user's id
	 * @return a list of all question published by the user
	 * @throws Exception
	 *             if fail
	 */
	public List<Question> getQuestionsByAuthor(int authorId) throws Exception;

	/**
	 * Create a new question
	 * 
	 * @param text
	 *            text of the question
	 * @param authorId
	 *            id of the author
	 * @param timestamp
	 *            time stamp of this question
	 * @return the new question
	 * @throws Exception
	 *             if fail
	 */
	public Question createQuestion(String text, int authorId, Timestamp timestamp) throws Exception;

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
	public int getQuestionAuthorId(int questionId) throws Exception;

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
	 * Get the newest question
	 * 
	 * @param size
	 *            the wanted size of the list
	 * @return a list of the newest questions
	 * @throws Exception
	 *             if fail
	 */
	public ArrayList<Question> getNewestQuestions(int size) throws Exception;

}
