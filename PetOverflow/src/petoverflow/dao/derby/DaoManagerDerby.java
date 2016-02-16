package petoverflow.dao.derby;

import java.sql.SQLException;

import petoverflow.dao.DaoManager;

public class DaoManagerDerby extends DaoManager {

	public static void init() throws ClassNotFoundException, SQLException {
		m_instance = new DaoManagerDerby();
	}

	protected DaoManagerDerby() throws ClassNotFoundException, SQLException {
		super();
		UserDaoDerby.init(this);
		QuestionDaoDerby.init(this);
		AnswerDaoDerby.init(this);
		QuestionVoteDaoDerby.init(this);
		AnswerVoteDaoDerby.init(this);
		TopicDaoDerby.init(this);

		m_userDao = UserDaoDerby.getInstance();
		m_questionDao = QuestionDaoDerby.getInstance();
		m_answerDao = AnswerDaoDerby.getInstance();
		m_questionVoteDao = QuestionVoteDaoDerby.getInstance();
		m_answerVoteDao = AnswerVoteDaoDerby.getInstance();
		m_topicDao = TopicDaoDerby.getInstance();
	}

}
