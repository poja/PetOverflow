package petoverflow.dao;

import java.util.List;

/**
 * The AnswerVoteDao interface provide a set of methods to manage and read all
 * votes to answers. All methods throws Exception in failure.
 */
public interface AnswerVoteDao {

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
