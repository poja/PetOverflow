package petoverflow.dao;

/**
 * The DaoManager class used to manage all DAO objects. It's contains all sub
 * DAO objects. This class is singleton.
 */
public abstract class DaoManager {

	protected UserDao m_userDao;

	protected QuestionDao m_questionDao;

	protected AnswerDao m_answerDao;

	protected QuestionVoteDao m_questionVoteDao;

	protected AnswerVoteDao m_answerVoteDao;

	protected TopicDao m_topicDao;

	protected static DaoManager m_instance;

	protected DaoManager() {
	}

	public static DaoManager getInstance() {
		if (m_instance == null) {
			throw new IllegalStateException("Dao manager wasn't initialized.");
		} else {
			return m_instance;
		}
	}

	public UserDao getUserDao() {
		return m_userDao;
	}

	public QuestionDao getQuestionDao() {
		return m_questionDao;
	}

	public AnswerDao getAnswerDao() {
		return m_answerDao;
	}

	public QuestionVoteDao getQuestionVoteDao() {
		return m_questionVoteDao;
	}

	public AnswerVoteDao getAnswerVoteDao() {
		return m_answerVoteDao;
	}

	public TopicDao getTopicDao() {
		return m_topicDao;
	}

}
