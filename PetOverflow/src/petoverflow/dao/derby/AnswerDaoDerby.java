package petoverflow.dao.derby;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import petoverflow.dao.Answer;
import petoverflow.dao.AnswerDao;
import petoverflow.dao.AnswerVoteDao;
import petoverflow.dao.exception.DerbyNotInitialized;
import petoverflow.dao.exception.NoSuchAnswerException;

/**
 * The AnswerDaoDerby class implements the AnswerDao with the Derby data base.
 */
public class AnswerDaoDerby implements AnswerDao {

	/**
	 * The votes DAO this DAO's answer are related to
	 */
	private final AnswerVoteDao m_answerVoteDao;

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
			throw new DerbyNotInitialized();
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
	public static void init(AnswerVoteDao answerVoteDao) throws ClassNotFoundException, SQLException {
		System.out.println("Initiating answers database connection");
		DerbyUtils.initTable(DerbyConfig.ANSWER_TABLE_NAME, DerbyConfig.ANSWER_TABLE_CREATE);
		m_instance = new AnswerDaoDerby(answerVoteDao);
	}

	/**
	 * Constructor (Private)
	 * 
	 * Used once to create the single instance
	 * 
	 * @param answerVoteDao
	 *            The votes DAO this DAO's answer are related to
	 */
	private AnswerDaoDerby(AnswerVoteDao answerVoteDao) {
		m_answerVoteDao = answerVoteDao;
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
		return new Answer(answerId, this, m_answerVoteDao);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.AnswerDao#exist(int)
	 */
	@Override
	public boolean exist(int answerId) throws SQLException {
		Connection conn = DerbyUtils.getConnection(DerbyConfig.ANSWER_TABLE_CREATE);
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			PreparedStatement s = conn.prepareStatement(
					"SELECT * FROM " + DerbyConfig.ANSWER_TABLE_NAME + " WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setInt(1, answerId);
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
	 * @see petoverflow.dao.AnswerDao#getAnswerText(int)
	 */
	@Override
	public String getAnswerText(int answerId) throws SQLException, NoSuchAnswerException {
		if (!exist(answerId)) {
			throw new NoSuchAnswerException();
		}

		Connection conn = DerbyUtils.getConnection(DerbyConfig.ANSWER_TABLE_CREATE);
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.TEXT + " FROM "
					+ DerbyConfig.ANSWER_TABLE_NAME + " WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setInt(1, answerId);
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
	 * @see petoverflow.dao.AnswerDao#getAnswerAuthorId(int)
	 */
	@Override
	public int getAnswerAuthorId(int answerId) throws SQLException, NoSuchAnswerException {
		if (!exist(answerId)) {
			throw new NoSuchAnswerException();
		}

		Connection conn = DerbyUtils.getConnection(DerbyConfig.ANSWER_TABLE_CREATE);
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.AUTHOR_ID + " FROM "
					+ DerbyConfig.ANSWER_TABLE_NAME + " WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setInt(1, answerId);
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
	 * @see petoverflow.dao.AnswerDao#getAnswerQuestionId(int)
	 */
	@Override
	public int getAnswerQuestionId(int answerId) throws SQLException, NoSuchAnswerException {
		if (!exist(answerId)) {
			throw new NoSuchAnswerException();
		}

		Connection conn = DerbyUtils.getConnection(DerbyConfig.ANSWER_TABLE_CREATE);
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.QUESTION_ID + " FROM "
					+ DerbyConfig.ANSWER_TABLE_NAME + " WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setInt(1, answerId);
			rs = s.executeQuery();
			return rs.getInt(DerbyConfig.QUESTION_ID);

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
	 * @see petoverflow.dao.AnswerDao#getAnswerTimestamp(int)
	 */
	@Override
	public Timestamp getAnswerTimestamp(int answerId) throws SQLException, NoSuchAnswerException {
		if (!exist(answerId)) {
			throw new NoSuchAnswerException();
		}

		Connection conn = DerbyUtils.getConnection(DerbyConfig.ANSWER_TABLE_CREATE);
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.TIMESTAMP + " FROM "
					+ DerbyConfig.ANSWER_TABLE_NAME + " WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setInt(1, answerId);
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
	 * @see petoverflow.dao.AnswerDao#getAnswersByUser(int)
	 */
	@Override
	public List<Answer> getAnswersByAuthor(int authorId) throws SQLException {
		Connection conn = DerbyUtils.getConnection(DerbyConfig.ANSWER_TABLE_CREATE);
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.ID + " FROM "
					+ DerbyConfig.ANSWER_TABLE_NAME + " WHERE " + DerbyConfig.AUTHOR_ID + " = ?");
			statements.add(s);
			s.setInt(1, authorId);
			rs = s.executeQuery();

			List<Answer> answersByUser = new ArrayList<Answer>();
			while (rs.next()) {
				int answerId = rs.getInt(DerbyConfig.ID);
				answersByUser.add(new Answer(answerId, this, m_answerVoteDao));
			}
			return answersByUser;

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
	 * @see petoverflow.dao.AnswerDao#getQuestionAnswers(int)
	 */
	@Override
	public List<Answer> getQuestionAnswers(int questionId) throws SQLException {
		Connection conn = DerbyUtils.getConnection(DerbyConfig.ANSWER_TABLE_CREATE);
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.ID + " FROM "
					+ DerbyConfig.ANSWER_TABLE_NAME + " WHERE " + DerbyConfig.QUESTION_ID + " = ?");
			statements.add(s);
			s.setInt(1, questionId);
			rs = s.executeQuery();

			List<Answer> questionAnswers = new ArrayList<Answer>();
			while (rs.next()) {
				int answerId = rs.getInt(DerbyConfig.ID);
				questionAnswers.add(new Answer(answerId, this, m_answerVoteDao));
			}
			return questionAnswers;

		} catch (SQLException e) {
			DerbyUtils.printSQLException(e);
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}
	}

}
