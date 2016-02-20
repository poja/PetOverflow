package petoverflow.dao.items;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import petoverflow.dao.DaoManager;
import petoverflow.dao.DaoObject;
import petoverflow.dao.QuestionDao;
import petoverflow.dao.QuestionVoteDao;
import petoverflow.dao.items.Vote.VoteType;
import petoverflow.dao.utility.Rated;
import petoverflow.dao.utility.Timestampable;
import petoverflow.dto.QuestionDto;

/**
 * The Question class represent a user question. This class uses DAO to get it's
 * data and most methods throw exceptions if the DAO fails.
 * 
 * @see QuestionDao
 * @see QuestionVoteDao
 */
public class Question extends DaoObject implements Rated, Timestampable {

	/**
	 * Id of this question
	 */
	private final int m_id;

	/**
	 * Constructor
	 * 
	 * This constructor is not meant to be used directly. It should be called
	 * through a questions DAO
	 * 
	 * @param id
	 *            if of this question
	 * @param questionDao
	 *            question DAO of this question
	 * @param questionVoteDao
	 *            question's votes DAO of this question
	 * @param topicDao
	 *            the question topics' DAO
	 */
	public Question(DaoManager daoManager, int id) {
		super(daoManager);
		m_id = id;
	}

	/**
	 * Get the id if the question
	 * 
	 * @return this question's id
	 */
	public int getId() {
		return m_id;
	}

	/**
	 * Get the text of this question
	 * 
	 * @return this question's text
	 * @throws Exception
	 *             if the DAO fails
	 */
	public String getText() throws Exception {
		return m_daoManager.getQuestionDao().getQuestionText(m_id);
	}

	/**
	 * Get the id of this question's author
	 * 
	 * @return this question's author's id
	 * @throws Exception
	 *             if the DAO fails
	 */
	public User getAuthor() throws Exception {
		return m_daoManager.getQuestionDao().getQuestionAuthor(m_id);
	}

	/**
	 * Get the rating of this question
	 * 
	 * @return this question's rating
	 * @throws Exception
	 *             if the DAO fails
	 */
	public double getRating() throws Exception {
		double ownQuestionRating = getVoteCount();
		if (Double.isNaN(ownQuestionRating)) {
			ownQuestionRating = 0;
		}
		double averageAnswerRating = 0;

		List<Answer> answers = m_daoManager.getAnswerDao().getQuestionAnswersAll(m_id);
		for (Answer answer : answers) {
			double answerRating = answer.getRating();
			if (Double.isNaN(answerRating)) {
				answerRating = 0;
			}
			averageAnswerRating += answerRating;
		}
		if (answers.size() != 0) {
			averageAnswerRating /= answers.size();
		}
		if (Double.isNaN(averageAnswerRating)) {
			averageAnswerRating = 0;
		}

		return 0.2 * ownQuestionRating + 0.8 * averageAnswerRating;
	}

	public double getVoteCount() throws Exception {
		List<Vote> questionVotes = m_daoManager.getQuestionVoteDao().getQuestionVotes(m_id);

		double rating = 0;
		for (Vote vote : questionVotes) {
			rating += (vote.getType() == VoteType.Up) ? 1 : -1;
		}

		return rating;
	}

	/**
	 * Get the time then this question was submitted
	 * 
	 * @return the time stamp of this question
	 * @throws Exception
	 *             if the DAO fails
	 */
	public Timestamp getTimestamp() throws Exception {
		return m_daoManager.getQuestionDao().getQuestionTimestamp(m_id);
	}

	/**
	 * Find the highest rated answer to this question
	 * 
	 * @return id of the best answer, of null if there aren't any
	 */
	public Integer getBestAnswer() throws Exception {
		return m_daoManager.getAnswerVoteDao().getBestAnswerForQuestion(m_id);
	}

	/**
	 * Get a list of this question related topics
	 * 
	 * @return this question's topics
	 * @throws Exception
	 *             if the DAO fails
	 */
	public List<Topic> getTopics() throws Exception {
		return m_daoManager.getTopicDao().getQuestionTopics(m_id);
	}

	/**
	 * Create a QuestionDto object that contains this question data
	 * 
	 * @param userId
	 *            the id of the user's, used to check the vote status
	 * @return QuestionDto that holds this question data
	 * @throws Exception
	 *             if the DAO fails
	 */
	public QuestionDto toQuestionDto(int userId) throws Exception {
		QuestionDto question = new QuestionDto();
		question.id = m_id;
		question.text = getText();
		question.authorId = getAuthor().getId();
		question.rating = getRating();
		question.voteCount = getVoteCount();
		question.timestamp = getTimestamp().getTime();
		question.bestAnswerId = getBestAnswer();

		List<Topic> topics = getTopics();
		question.topics = new ArrayList<String>();
		for (Topic topic : topics) {
			question.topics.add(topic.getName());
		}

		int voteStatus = 0;
		List<Vote> votes = m_daoManager.getQuestionVoteDao().getQuestionVotes(m_id);
		for (Vote vote : votes) {
			if (vote.getVoterId() == userId) {
				voteStatus = vote.getType() == VoteType.Up ? 1 : -1;
				break;
			}
		}
		question.voteStatus = voteStatus;

		return question;
	}

	@Override
	public int hashCode() {
		return m_id;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		} else if (o == this) {
			return true;
		} else if (!(o instanceof Question)) {
			return false;
		}

		Question other = (Question) o;
		return m_id == other.m_id;
	}

}
