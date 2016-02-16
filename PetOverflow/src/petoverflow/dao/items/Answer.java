package petoverflow.dao.items;

import java.sql.Timestamp;
import java.util.List;

import petoverflow.dao.AnswerDao;
import petoverflow.dao.AnswerVoteDao;
import petoverflow.dao.DaoManager;
import petoverflow.dao.DaoObject;
import petoverflow.dao.items.Vote.VoteType;
import petoverflow.dao.utility.Rated;
import petoverflow.dao.utility.Timestampable;
import petoverflow.dto.AnswerDto;

/**
 * The Answer class represent a user answer to question. This class uses DAO to
 * get it's data and most of it's methods throw exceptions if the DAO fails.
 * 
 * @see Question
 * @see AnswerDao
 * @see AnswerVoteDao
 */
public class Answer extends DaoObject implements Rated, Timestampable {

	/**
	 * Id of the answer
	 */
	private final int m_id;

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
	public Answer(DaoManager daoManager, int id) {
		super(daoManager);
		m_id = id;
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
		return m_daoManager.getAnswerDao().getAnswerText(m_id);
	}

	/**
	 * Get the id of this answer's author
	 * 
	 * @return the id of this answer's author
	 * @throws Exception
	 *             if DAO fails
	 */
	public User getAuthor() throws Exception {
		return m_daoManager.getAnswerDao().getAnswerAuthor(m_id);
	}

	/**
	 * Get the total rating of this answer
	 * 
	 * @return the rating of this answer
	 * @throws Exception
	 *             if DAO fails
	 */
	public double getRating() throws Exception {
		List<Vote> answerVotes = m_daoManager.getAnswerVoteDao().getAnswerVotes(m_id);

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
	public Question getQuestion() throws Exception {
		return m_daoManager.getAnswerDao().getAnswerQuestion(m_id);
	}

	/**
	 * Get the time when this answer was submitted
	 * 
	 * @return the time stamp of this answer
	 * @throws Exception
	 *             if DAO fails
	 */
	public Timestamp getTimestamp() throws Exception {
		return m_daoManager.getAnswerDao().getAnswerTimestamp(m_id);
	}

	public AnswerDto toAnswerDto(int userId) throws Exception {
		AnswerDto answer = new AnswerDto();
		answer.id = m_id;
		answer.text = getText();
		answer.authorId = getAuthor().getId();
		answer.rating = getRating();
		answer.timestamp = getTimestamp();
		answer.questionId = getQuestion().getId();

		int voteStatus = 0;
		List<Vote> votes = m_daoManager.getAnswerVoteDao().getAnswerVotes(m_id);
		for (Vote vote : votes) {
			if (vote.getVoterId() == userId) {
				voteStatus = vote.getType() == VoteType.Up ? 1 : -1;
				break;
			}
		}
		answer.voteStatus = voteStatus;

		return answer;
	}

	public int hashCode() {
		return m_id;
	}

	public boolean equals(Object o) {
		if (o == null) {
			return false;
		} else if (o == this) {
			return true;
		} else if (!(o instanceof Answer)) {
			return false;
		}

		Answer other = (Answer) o;
		return m_id == other.m_id;
	}

}
