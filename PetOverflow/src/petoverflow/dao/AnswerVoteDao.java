package petoverflow.dao;

import java.util.List;

/**
 * The AnswerVoteDao interface provide a set of methods to manage and read all
 * votes to answers. All methods throws Exception in failure.
 */
public interface AnswerVoteDao {

	/**
	 * Add a vote to a answer
	 * 
	 * @param answerId
	 *            the answer's id
	 * @param vote
	 *            the vote
	 * @throws Exception
	 *             if fail
	 */
	public void addVote(int answerId, Vote vote) throws Exception;

	/**
	 * Remove a vote from a answer
	 * 
	 * @param answerId
	 *            the answer's id
	 * @param voterId
	 *            the voter's id
	 * @throws Exception
	 *             if fail
	 */
	public void removeVote(int answerId, int voterId) throws Exception;

	/**
	 * Get all votes to a specific answer
	 * 
	 * @param answerId
	 *            the answer's id
	 * @return a list of all votes to the answer
	 * @throws Exception
	 *             if fail
	 */
	public List<Vote> getAnswerVotes(int answerId) throws Exception;

}
