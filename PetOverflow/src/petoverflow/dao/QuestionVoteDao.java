package petoverflow.dao;

import java.util.List;

/**
 * The QuestionVoteDao interface provide a set of methods to manage and read all
 * votes to questions. All methods throws Exception in failure.
 */
public interface QuestionVoteDao {

	/**
	 * Add a vote to a question
	 * 
	 * @param questionId
	 *            the question's id
	 * @param vote
	 *            the vote
	 * @throws Exception
	 *             if fail
	 */
	public void addVote(int questionId, Vote vote) throws Exception;

	/**
	 * Remove a vote from a question
	 * 
	 * @param questionId
	 *            the question's id
	 * @param voterId
	 *            the voter's id
	 * @throws Exception
	 *             if fail
	 */
	public void removeVote(int questionId, int voterId) throws Exception;

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
