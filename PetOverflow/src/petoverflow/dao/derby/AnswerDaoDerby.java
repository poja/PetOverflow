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
import petoverflow.dao.AnswerDao;
import petoverflow.dao.DaoManager;
import petoverflow.dao.DaoObject;
import petoverflow.dao.items.Answer;
import petoverflow.dao.items.Question;
import petoverflow.dao.items.User;
import petoverflow.dao.utility.exception.NoSuchAnswerException;

/**
 * The AnswerDaoDerby class implements the AnswerDao with the Derby data base.
 */
public class AnswerDaoDerby extends DaoObject implements AnswerDao {

	/**
	 * The single instance of this class
	 */
	private static AnswerDaoDerby m_instance;

	/**
	 * Get the single instance of this class
	 * 
	 * @return the single instance of this class
	 */
	public static AnswerDaoDerby getInstance() {
		if (m_instance == null) {
			throw new IllegalStateException("Answers dao wasn't initialized.");
		}
		return m_instance;
	}

	/**
	 * Initialize this derby DAO
	 * 
	 * @param answerVoteDao
	 *            The votes DAO this DAO's answer are related to
	 * @throws ClassNotFoundException
	 *             if derby is not installed
	 * @throws SQLException
	 *             if failed to created this DAO tables
	 */
	public static void init(DaoManager daoManager) throws ClassNotFoundException, SQLException {
		System.out.println("Initiating answers database connection");
		DerbyUtils.initTable(DerbyConfig.DB_NAME, DerbyConfig.ANSWER_TABLE_CREATE);
		m_instance = new AnswerDaoDerby(daoManager);
	}

	/**
	 * Constructor (Private)
	 * 
	 * Used once to create the single instance
	 * 
	 * @param answerVoteDao
	 *            The votes DAO this DAO's answer are related to
	 */
	private AnswerDaoDerby(DaoManager daoManager) {
		super(daoManager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.AnswerDao#getAnswer(int)
	 */
	@Override
	public Answer getAnswer(int answerId) throws SQLException, NoSuchAnswerException {
		if (!exist(answerId)) {
			throw new NoSuchAnswerException();
		}
		return new Answer(getDaoManager(), answerId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.AnswerDao#exist(int)
	 */
	@Override
	public boolean exist(int answerId) throws SQLException {
		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement(
					"SELECT * FROM " + DerbyConfig.ANSWER_TABLE_NAME + " WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setInt(1, answerId);
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
	 * @see petoverflow.dao.AnswerDao#createAnswer(String, int, int)
	 */
	@Override
	public Answer createAnswer(String text, int authorId, int questionId) throws SQLException {
		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement("INSERT INTO " + DerbyConfig.ANSWER_TABLE_NAME + " ("
					+ DerbyConfig.TEXT + "," + DerbyConfig.AUTHOR_ID + "," + DerbyConfig.QUESTION_ID + ","
					+ DerbyConfig.TIMESTAMP + ") VALUES (?, ?, ?, ?)", new String[] { DerbyConfig.ID });
			statements.add(s);
			s.setString(1, text);
			s.setInt(2, authorId);
			s.setInt(3, questionId);
			Timestamp now = new Timestamp(Calendar.getInstance().getTime().getTime());
			s.setTimestamp(4, now);
			s.executeUpdate();
			rs = s.getGeneratedKeys();

			if (!rs.next()) {
				throw new SQLException("Unexpected error");
			}
			int id = rs.getInt(1);
			return new Answer(getDaoManager(), id);

		} catch (SQLException e) {
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.AnswerDao#getAnswerText(int)
	 */
	@Override
	public String getAnswerText(int answerId) throws SQLException, NoSuchAnswerException {
		if (!exist(answerId)) {
			throw new NoSuchAnswerException();
		}

		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.TEXT + " FROM "
					+ DerbyConfig.ANSWER_TABLE_NAME + " WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setInt(1, answerId);
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
	 * @see petoverflow.dao.AnswerDao#getAnswerAuthorId(int)
	 */
	@Override
	public User getAnswerAuthor(int answerId) throws Exception {
		if (!exist(answerId)) {
			throw new NoSuchAnswerException();
		}

		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.AUTHOR_ID + " FROM "
					+ DerbyConfig.ANSWER_TABLE_NAME + " WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setInt(1, answerId);
			rs = s.executeQuery();
			if (!rs.next()) {
				throw new SQLException("Unexpected error");
			}
			int userId = rs.getInt(DerbyConfig.AUTHOR_ID);
			return getDaoManager().getUserDao().getUser(userId);

		} catch (Exception e) {
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.AnswerDao#getAnswerQuestionId(int)
	 */
	@Override
	public Question getAnswerQuestion(int answerId) throws Exception {
		if (!exist(answerId)) {
			throw new NoSuchAnswerException();
		}

		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.QUESTION_ID + " FROM "
					+ DerbyConfig.ANSWER_TABLE_NAME + " WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setInt(1, answerId);
			rs = s.executeQuery();
			if (!rs.next()) {
				throw new SQLException("Unexpected error");
			}
			int questionId = rs.getInt(DerbyConfig.QUESTION_ID);
			return getDaoManager().getQuestionDao().getQuestion(questionId);

		} catch (Exception e) {
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.AnswerDao#getAnswerTimestamp(int)
	 */
	@Override
	public Timestamp getAnswerTimestamp(int answerId) throws SQLException, NoSuchAnswerException {
		if (!exist(answerId)) {
			throw new NoSuchAnswerException();
		}

		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.TIMESTAMP + " FROM "
					+ DerbyConfig.ANSWER_TABLE_NAME + " WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setInt(1, answerId);
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
	 * @see petoverflow.dao.AnswerDao#getAnswersByUser(int)
	 */
	@Override
	public List<Answer> getAnswersByAuthor(int authorId, int size, int offset) throws SQLException {
		List<Answer> answersByUser = getAnswersByAuthorAll(authorId);
		return Utility.cutList(answersByUser, size, offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.AnswerDao#getAnswersByAuthorAll(int)
	 */
	public List<Answer> getAnswersByAuthorAll(int authorId) throws SQLException {
		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.ID + " FROM "
					+ DerbyConfig.ANSWER_TABLE_NAME + " WHERE " + DerbyConfig.AUTHOR_ID + " = ?");
			statements.add(s);
			s.setInt(1, authorId);
			rs = s.executeQuery();

			List<Answer> answersByUser = new ArrayList<Answer>();
			while (rs.next()) {
				int answerId = rs.getInt(DerbyConfig.ID);
				answersByUser.add(new Answer(getDaoManager(), answerId));
			}
			return answersByUser;

		} catch (SQLException e) {
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.AnswerDao#getQuestionAnswers(int)
	 */
	@Override
	public List<Answer> getQuestionAnswers(int questionId, int size, int offset) throws SQLException {
		List<Answer> questionAnswers = getQuestionAnswersAll(questionId);
		return Utility.cutList(questionAnswers, size, offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.AnswerDao#getQuestionAnswersAll(int)
	 */
	@Override
	public List<Answer> getQuestionAnswersAll(int questionId) throws SQLException {
		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.ID + " FROM "
					+ DerbyConfig.ANSWER_TABLE_NAME + " WHERE " + DerbyConfig.QUESTION_ID + " = ?");
			statements.add(s);
			s.setInt(1, questionId);
			rs = s.executeQuery();

			List<Answer> questionAnswers = new ArrayList<Answer>();
			while (rs.next()) {
				int answerId = rs.getInt(DerbyConfig.ID);
				questionAnswers.add(new Answer(getDaoManager(), answerId));
			}

			Utility.sortByRating(questionAnswers);
			return questionAnswers;

		} catch (SQLException e) {
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}
	}

}
