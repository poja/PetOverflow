package petoverflow.dao.derby;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import petoverflow.Utility;
import petoverflow.dao.DaoManager;
import petoverflow.dao.DaoObject;
import petoverflow.dao.QuestionDao;
import petoverflow.dao.items.Question;
import petoverflow.dao.items.User;
import petoverflow.dao.utility.exception.NoSuchQuestionException;

/**
 * The QuestionDaoDerby class implements the QuestionDao with Derby DB.
 */
public class QuestionDaoDerby extends DaoObject implements QuestionDao {

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
			throw new IllegalStateException("Questions dao wasn't initialized.");
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
	public static void init(DaoManager daoManager) throws ClassNotFoundException, SQLException {
		System.out.println("Initiating questions database connection");
		DerbyUtils.initTable(DerbyConfig.DB_NAME, DerbyConfig.QUESTION_TABLE_CREATE);
		m_instance = new QuestionDaoDerby(daoManager);
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
	private QuestionDaoDerby(DaoManager daoManager) {
		super(daoManager);
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
		return new Question(m_daoManager, questionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.QuestionDao#exist(int)
	 */
	@Override
	public boolean exist(int questionId) throws SQLException {
		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement(
					"SELECT * FROM " + DerbyConfig.QUESTION_TABLE_NAME + " WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setInt(1, questionId);
			rs = s.executeQuery();
			return rs.next();

		} catch (SQLException e) {
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
	public List<Question> getQuestionsByAuthor(int authorId, int size, int offset) throws SQLException {
		List<Question> userQuestions = getQuestionsByAuthorAll(authorId);
		return Utility.cutList(userQuestions, size, offset);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.QuestionDao#getQuestionsByAuthorAll(int)
	 */
	public List<Question> getQuestionsByAuthorAll(int authorId) throws SQLException {
		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement(
					"SELECT * FROM " + DerbyConfig.QUESTION_TABLE_NAME + " WHERE " + DerbyConfig.AUTHOR_ID + " = ?");
			statements.add(s);
			s.setInt(1, authorId);
			rs = s.executeQuery();

			List<Question> userQuestions = new ArrayList<Question>();
			while (rs.next()) {
				int id = rs.getInt(DerbyConfig.ID);
				userQuestions.add(new Question(m_daoManager, id));
			}
			return userQuestions;

		} catch (SQLException e) {
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
	public Question createQuestion(String text, int userId, List<String> topics) throws Exception {
		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement(
					"INSERT INTO " + DerbyConfig.QUESTION_TABLE_NAME + " (" + DerbyConfig.TEXT + ","
							+ DerbyConfig.AUTHOR_ID + "," + DerbyConfig.TIMESTAMP + ") VALUES (?, ?, ?)",
					new String[] { DerbyConfig.ID });
			statements.add(s);
			s.setString(1, text);
			s.setInt(2, userId);
			Timestamp now = new Timestamp(Calendar.getInstance().getTime().getTime());
			s.setTimestamp(3, now);
			s.executeUpdate();
			rs = s.getGeneratedKeys();

			if (!rs.next()) {
				throw new SQLException("Unexpected error");
			}
			System.out.println(rs);
			int id = rs.getInt(1);
			Question question = new Question(m_daoManager, id);

			m_daoManager.getTopicDao().setTopics(question.getId(), topics);

			return question;

		} catch (SQLException e) {
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

		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.TEXT + " FROM "
					+ DerbyConfig.QUESTION_TABLE_NAME + " WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setInt(1, questionId);
			rs = s.executeQuery();
			if (!rs.next()) {
				throw new SQLException("Unexpected error");
			}
			return rs.getString(DerbyConfig.TEXT);

		} catch (SQLException e) {
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
	public User getQuestionAuthor(int questionId) throws Exception {
		if (!exist(questionId)) {
			throw new NoSuchQuestionException();
		}

		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.AUTHOR_ID + " FROM "
					+ DerbyConfig.QUESTION_TABLE_NAME + " WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setInt(1, questionId);
			rs = s.executeQuery();
			if (!rs.next()) {
				throw new SQLException("Unexpected error");
			}
			int userId = rs.getInt(DerbyConfig.AUTHOR_ID);
			return m_daoManager.getUserDao().getUser(userId);

		} catch (Exception e) {
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

		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.TIMESTAMP + " FROM "
					+ DerbyConfig.QUESTION_TABLE_NAME + " WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setInt(1, questionId);
			rs = s.executeQuery();
			if (!rs.next()) {
				throw new SQLException("Unexpected error");
			}
			return rs.getTimestamp(DerbyConfig.TIMESTAMP);

		} catch (SQLException e) {
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
	public List<Question> getNewestQuestions(int size, int offset) throws SQLException {
		List<Question> questions = getAllQuestion();
		Utility.sortByTimestamp(questions);
		return Utility.cutList(questions, size, offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.QuestionDao#getBestQuestions(int, int)
	 */
	@Override
	public List<Question> getBestQuestions(int size, int offset) throws SQLException {
		List<Question> questions = getAllQuestion();
		Utility.sortByRating(questions);
		return Utility.cutList(questions, size, offset);
	}

	private List<Question> getAllQuestion() throws SQLException {
		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		List<Question> questions = new ArrayList<Question>();
		;
		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn
					.prepareStatement("SELECT " + DerbyConfig.ID + " FROM " + DerbyConfig.QUESTION_TABLE_NAME);
			statements.add(s);
			rs = s.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(DerbyConfig.ID);
				Question question = new Question(m_daoManager, id);
				questions.add(question);
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}
		return questions;
	}

}
