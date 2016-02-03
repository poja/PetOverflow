package petoverflow.dao.derby;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import petoverflow.dao.Question;
import petoverflow.dao.QuestionDao;
import petoverflow.dao.QuestionVoteDao;
import petoverflow.dao.TopicDao;
import petoverflow.dao.exception.DerbyNotInitialized;
import petoverflow.dao.exception.NoSuchQuestionException;

/**
 * The QuestionDaoDerby class implements the QuestionDao with Derby DB.
 */
public class QuestionDaoDerby implements QuestionDao {

	/**
	 * The votes DAO this DAO's questions are related to
	 */
	private final QuestionVoteDao m_questionVoteDao;

	/**
	 * The topics DAO this DAO's questions are related to
	 */
	private final TopicDao m_topicDao;

	/**
	 * The single instance of this class
	 */
	private static QuestionDaoDerby m_instance;

	/**
	 * Get the single instance of this class
	 * 
	 * @return the single instance
	 */
	public static QuestionDaoDerby getInstance() {
		if (m_instance == null) {
			throw new DerbyNotInitialized();
		}
		return m_instance;
	}

	/**
	 * Initialize this derby DAO
	 * 
	 * @param questionVoteDao
	 *            The votes DAO this DAO's questions are related to
	 * @param topicDao
	 *            The topics DAO this DAO's questions are related to
	 * @throws ClassNotFoundException
	 *             if derby is not installed
	 * @throws SQLException
	 *             if failed to created this DAO tables
	 */
	public static void init(QuestionVoteDao questionVoteDao, TopicDao topicDao)
			throws ClassNotFoundException, SQLException {
		System.out.println("Initiating questions database connection");
		DerbyUtils.initTable(DerbyConfig.QUESTION_TABLE_NAME, DerbyConfig.QUESTION_TABLE_CREATE);
		m_instance = new QuestionDaoDerby(questionVoteDao, topicDao);
	}

	/**
	 * Constructor (Private)
	 * 
	 * Used once to create the single instance
	 * 
	 * @param questionVoteDao
	 * 
	 * @param topicDao
	 */
	private QuestionDaoDerby(QuestionVoteDao questionVoteDao, TopicDao topicDao) {
		m_questionVoteDao = questionVoteDao;
		m_topicDao = topicDao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.QuestionDao#getQuestion(int)
	 */
	@Override
	public Question getQuestion(int questionId) throws SQLException, NoSuchQuestionException {
		if (!exist(questionId)) {
			throw new NoSuchQuestionException();
		}
		return new Question(questionId, this, m_questionVoteDao, m_topicDao);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.QuestionDao#exist(int)
	 */
	@Override
	public boolean exist(int questionId) throws SQLException {
		Connection conn = DerbyUtils.getConnection(DerbyConfig.QUESTION_TABLE_CREATE);
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			PreparedStatement s = conn.prepareStatement(
					"SELECT * FROM " + DerbyConfig.QUESTION_TABLE_NAME + " WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setInt(1, questionId);
			rs = s.executeQuery();
			return rs.next();

		} catch (SQLException e) {
			DerbyUtils.printSQLException(e);
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.QuestionDao#getQuestionsByUser(int)
	 */
	@Override
	public List<Question> getQuestionsByAuthor(int authorId) throws SQLException {
		Connection conn = DerbyUtils.getConnection(DerbyConfig.QUESTION_TABLE_CREATE);
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			PreparedStatement s = conn.prepareStatement(
					"SELECT * FROM " + DerbyConfig.QUESTION_TABLE_NAME + " WHERE " + DerbyConfig.AUTHOR_ID + " = ?");
			statements.add(s);
			s.setInt(1, authorId);
			rs = s.executeQuery();

			List<Question> userQuestions = new ArrayList<Question>();
			while (rs.next()) {
				int id = rs.getInt(DerbyConfig.ID);
				userQuestions.add(new Question(id, this, m_questionVoteDao, m_topicDao));
			}
			return userQuestions;

		} catch (SQLException e) {
			DerbyUtils.printSQLException(e);
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.QuestionDao#createQuestion(java.lang.String, int,
	 * java.sql.Timestamp)
	 */
	@Override
	public Question createQuestion(String text, int userId, Timestamp timestamp) throws SQLException {
		Connection conn = DerbyUtils.getConnection(DerbyConfig.QUESTION_TABLE_CREATE);
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			PreparedStatement s = conn
					.prepareStatement("INSERT INTO " + DerbyConfig.QUESTION_TABLE_NAME + " (" + DerbyConfig.TEXT + ","
							+ DerbyConfig.AUTHOR_ID + "," + DerbyConfig.TIMESTAMP + ") VALUES (?, ?, ?)");
			statements.add(s);
			s.setString(1, text);
			s.setInt(2, userId);
			s.setTimestamp(3, timestamp);
			rs = s.executeQuery();

			int id = rs.getInt(DerbyConfig.ID);
			return new Question(id, this, m_questionVoteDao, m_topicDao);

		} catch (SQLException e) {
			DerbyUtils.printSQLException(e);
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.QuestionDao#getQuestionText(int)
	 */
	@Override
	public String getQuestionText(int questionId) throws SQLException, NoSuchQuestionException {
		if (!exist(questionId)) {
			throw new NoSuchQuestionException();
		}

		Connection conn = DerbyUtils.getConnection(DerbyConfig.QUESTION_TABLE_CREATE);
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.TEXT + " FROM "
					+ DerbyConfig.QUESTION_TABLE_NAME + " WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setInt(1, questionId);
			rs = s.executeQuery();

			return rs.getString(DerbyConfig.TEXT);

		} catch (SQLException e) {
			DerbyUtils.printSQLException(e);
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.QuestionDao#getQuestionAuthorId(int)
	 */
	@Override
	public int getQuestionAuthorId(int questionId) throws SQLException, NoSuchQuestionException {
		if (!exist(questionId)) {
			throw new NoSuchQuestionException();
		}

		Connection conn = DerbyUtils.getConnection(DerbyConfig.QUESTION_TABLE_CREATE);
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.AUTHOR_ID + " FROM "
					+ DerbyConfig.QUESTION_TABLE_NAME + " WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setInt(1, questionId);
			rs = s.executeQuery();

			return rs.getInt(DerbyConfig.AUTHOR_ID);

		} catch (SQLException e) {
			DerbyUtils.printSQLException(e);
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.QuestionDao#getQuestionTimestamp(int)
	 */
	@Override
	public Timestamp getQuestionTimestamp(int questionId) throws SQLException, NoSuchQuestionException {
		if (!exist(questionId)) {
			throw new NoSuchQuestionException();
		}

		Connection conn = DerbyUtils.getConnection(DerbyConfig.QUESTION_TABLE_CREATE);
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.TIMESTAMP + " FROM "
					+ DerbyConfig.QUESTION_TABLE_NAME + " WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setInt(1, questionId);
			rs = s.executeQuery();

			return rs.getTimestamp(DerbyConfig.TIMESTAMP);

		} catch (SQLException e) {
			DerbyUtils.printSQLException(e);
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.QuestionDao#getNewestQuestions(int)
	 */
	@Override
	public ArrayList<Question> getNewestQuestions(int size) {
		// TODO Auto-generated method stub
		return null;
	}

}
