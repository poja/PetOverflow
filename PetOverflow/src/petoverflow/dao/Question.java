package petoverflow.dao;

import java.sql.Timestamp;
import java.util.List;

import petoverflow.dao.Vote.VoteType;
import petoverflow.dto.QuestionDto;

/**
 * The Question class represent a user question. This class uses DAO to get it's
 * data and most methods throw exceptions if the DAO fails.
 * 
 * @see QuestionDao
 * @see QuestionVoteDao
 */
public class Question {

	/**
	 * Id of this question
	 */
	private final int m_id;

	/**
	 * DAO of this question. Contains all data about this question
	 */
	private final QuestionDao m_questionDao;

	/**
	 * DAO of this question's votes. Contains all votes to this question
	 */
	private final QuestionVoteDao m_questionVoteDao;

	/**
	 * DAO of this question's topics. Contains all topics related to this
	 * question
	 */
	private final TopicDao m_topicDao;

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
	public Question(int id, QuestionDao questionDao, QuestionVoteDao questionVoteDao, TopicDao topicDao) {
		m_id = id;
		m_questionDao = questionDao;
		m_questionVoteDao = questionVoteDao;
		m_topicDao = topicDao;
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
		return m_questionDao.getQuestionText(m_id);
	}

	/**
	 * Get the id of this question's author
	 * 
	 * @return this question's author's id
	 * @throws Exception
	 *             if the DAO fails
	 */
	public int getAuthorId() throws Exception {
		return m_questionDao.getQuestionAuthorId(m_id);
	}

	/**
	 * Get the rating of this question
	 * 
	 * @return this question's rating
	 * @throws Exception
	 *             if the DAO fails
	 */
	public double getRating() throws Exception {
		List<Vote> questionVotes = m_questionVoteDao.getQuestionVotes(m_id);

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
		return m_questionDao.getQuestionTimestamp(m_id);
	}

	/**
	 * Get a list of this question related topics
	 * 
	 * @return this question's topics
	 * @throws Exception
	 *             if the DAO fails
	 */
	public List<String> getTopics() throws Exception {
		return m_topicDao.getQuestionTopics(m_id);
	}

	/**
	 * Create a QuestionDto object that contains this question data
	 * 
	 * @return QuestionDto that holds this question data
	 * @throws Exception
	 *             if the DAO fails
	 */
	public QuestionDto toQuestionDto() throws Exception {
		QuestionDto question = new QuestionDto();
		question.id = m_id;
		question.text = getText();
		question.authorId = getAuthorId();
		question.rating = getRating();
		question.timeStamp = getTimestamp();
		question.topics = getTopics();
		return question;
	}

}
