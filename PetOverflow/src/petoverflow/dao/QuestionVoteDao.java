package petoverflow.dao;

import java.util.List;

/**
 * The QuestionVoteDao interface provide a set of methods to manage and read all
 * votes to questions. All methods throws Exception in failure.
 */
public interface QuestionVoteDao {

	/**
	 * Get all votes to a specific question
	 * 
	 * @param questionId
	 *            the question's id
	 * @return a list of all votes to the question
	 * @throws Exception
	 *             if fail
	 */
	public List<Vote> getQuestionVotes(int questionId) throws Exception;

}
