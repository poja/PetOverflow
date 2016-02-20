package petoverflow.dao;

public abstract class DaoManager {

	protected UserDao m_userDao;

	protected QuestionDao m_questionDao;

	protected AnswerDao m_answerDao;

	protected QuestionVoteDao m_questionVoteDao;

	protected AnswerVoteDao m_answerVoteDao;

	protected TopicDao m_topicDao;

	protected static DaoManager m_instance;

	public static DaoManager getInstance() {
		if (m_instance == null) {
			throw new IllegalStateException("Dao manager wasn't initialized.");
		} else {
			return m_instance;
		}
	}

	public static boolean isInitialized() {
		return m_instance != null;
	}

	protected DaoManager() {
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
