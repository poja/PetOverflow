package petoverflow.dao;

import java.sql.Timestamp;
import java.util.List;

import petoverflow.dao.Vote.VoteType;
import petoverflow.dto.AnswerDto;

/**
 * The Answer class represent a user answer to question. This class uses DAO to
 * get it's data and most of it's methods throw exceptions if the DAO fails.
 * 
 * @see Question
 * @see AnswerDao
 * @see AnswerVoteDao
 */
public class Answer {

	/**
	 * Id of the answer
	 */
	private final int m_id;

	/**
	 * The answer's DAO that contains the data of the answer
	 */
	private final AnswerDao m_answerDao;

	/**
	 * The answer's votes DAO that contains all votes to this answer
	 */
	private final AnswerVoteDao m_answerVoteDao;

	/**
	 * Constructor
	 * 
	 * This constructor is not meant to be used directly. It should be called
	 * through an answers DAO
	 * 
	 * @param id
	 *            id of the answer
	 * @param answerDao
	 *            the answer's DAO
	 * @param answerVoteDao
	 *            the answer's votes DAO
	 */
	public Answer(int id, AnswerDao answerDao, AnswerVoteDao answerVoteDao) {
		m_id = id;
		m_answerDao = answerDao;
		m_answerVoteDao = answerVoteDao;
	}

	/**
	 * Get the id of this answer
	 * 
	 * @return the id of this answer
	 */
	public int getId() {
		return m_id;
	}

	/**
	 * Get the text of this answer
	 * 
	 * @return the text of this answer
	 * @throws Exception
	 *             if DAO fails
	 */
	public String getText() throws Exception {
		return m_answerDao.getAnswerText(m_id);
	}

	/**
	 * Get the id of this answer's author
	 * 
	 * @return the id of this answer's author
	 * @throws Exception
	 *             if DAO fails
	 */
	public int getAuthorId() throws Exception {
		return m_answerDao.getAnswerAuthorId(m_id);
	}

	/**
	 * Get the total rating of this answer
	 * 
	 * @return the rating of this answer
	 * @throws Exception
	 *             if DAO fails
	 */
	public double getRating() throws Exception {
		List<Vote> answerVotes = m_answerVoteDao.getAnswerVotes(m_id);

		double rating = 0;
		for (Vote vote : answerVotes) {
			rating += (vote.getType() == VoteType.Up) ? 1 : -1;
		}

		return rating;
	}

	/**
	 * Get the id of the question this answer was submitted to
	 * 
	 * @return if of this answer's question
	 * @throws Exception
	 *             if DAO fails
	 */
	public int getQuestionId() throws Exception {
		return m_answerDao.getAnswerQuestionId(m_id);
	}

	/**
	 * Get the time when this answer was submitted
	 * 
	 * @return the time stamp of this answer
	 * @throws Exception
	 *             if DAO fails
	 */
	public Timestamp getTimestamp() throws Exception {
		return m_answerDao.getAnswerTimestamp(m_id);
	}

	public AnswerDto toAnswerDto(int userId) throws Exception {
		AnswerDto answer = new AnswerDto();
		answer.id = m_id;
		answer.text = getText();
		answer.authorId = getAuthorId();
		answer.rating = getRating();
		answer.timestamp = getTimestamp();
		answer.questionId = getQuestionId();

		int voteStatus = 0;
		List<Vote> votes = m_answerVoteDao.getAnswerVotes(m_id);
		for (Vote vote : votes) {
			if (vote.getVoterId() == userId) {
				voteStatus = vote.getType() == VoteType.Up ? 1 : -1;
				break;
			}
		}
		answer.voteStatus = voteStatus;

		return answer;
	}

}
