package petoverflow.dao.derby;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import petoverflow.dao.DaoManager;
import petoverflow.dao.DaoObject;
import petoverflow.dao.QuestionVoteDao;
import petoverflow.dao.items.Vote;
import petoverflow.dao.items.Vote.VoteType;

/**
 * The QuestionVoteDaoDerby class implements the QuestionVoteDao interface with
 * Derby DB.
 */
public class QuestionVoteDaoDerby extends DaoObject implements QuestionVoteDao {

	/**
	 * The single instance of this class
	 */
	private static QuestionVoteDaoDerby m_instance;

	/**
	 * Get the single instance of this class
	 * 
	 * @return the single instance of this class
	 */
	public static QuestionVoteDaoDerby getInstance() {
		if (m_instance == null) {
			throw new IllegalStateException("Questions votes dao wasn't initialized.");
		}
		return m_instance;
	}

	/**
	 * Initialize this Derby DAO
	 * 
	 * @throws ClassNotFoundException
	 *             if derby is not installed
	 * @throws SQLException
	 *             if failed to created this DAO tables
	 */
	public static void init(DaoManager daoManager) throws ClassNotFoundException, SQLException {
		System.out.println("Initiating questions votes database connection");
		DerbyUtils.initTable(DerbyConfig.DB_NAME, DerbyConfig.QUESTION_VOTE_TABLE_CREATE);
		m_instance = new QuestionVoteDaoDerby(daoManager);
	}

	/**
	 * Constructor (Private)
	 * 
	 * Used once to create the single instance of this class
	 */
	private QuestionVoteDaoDerby(DaoManager daoManager) {
		super(daoManager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.QuestionVoteDao#addVote(int, petoverflow.dao.Vote)
	 */
	public void addVote(int questionId, Vote vote) throws SQLException {
		try {
			if (getDaoManager().getQuestionDao().getQuestion(questionId).getAuthor().getId() == vote.getVoterId()) {
				// Can vote to yourself
				return;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}
		// Remove previous vote
		removeVote(questionId, vote.getVoterId());

		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement(
					"INSERT INTO " + DerbyConfig.QUESTION_VOTE_TABLE_NAME + " (" + DerbyConfig.VOTER_ID + ","
							+ DerbyConfig.QUESTION_ID + "," + DerbyConfig.VOTE_TYPE + ") VALUES (?, ?, ?)");
			statements.add(s);
			s.setInt(1, vote.getVoterId());
			s.setInt(2, questionId);
			s.setBoolean(3, vote.getType() == VoteType.Up);
			s.executeUpdate();

		} catch (SQLException e) {
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.QuestionVoteDao#removeVote(int, int)
	 */
	public void removeVote(int questionId, int voterId) throws SQLException {
		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement("DELETE FROM " + DerbyConfig.QUESTION_VOTE_TABLE_NAME
					+ " WHERE " + DerbyConfig.VOTER_ID + " = ? AND " + DerbyConfig.QUESTION_ID + " = ?");
			statements.add(s);
			s.setInt(1, voterId);
			s.setInt(2, questionId);
			s.executeUpdate();

		} catch (SQLException e) {
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.QuestionVoteDao#getQuestionVotes(int)
	 */
	@Override
	public List<Vote> getQuestionVotes(int questionId) throws SQLException {
		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement("SELECT * FROM " + DerbyConfig.QUESTION_VOTE_TABLE_NAME
					+ " WHERE " + DerbyConfig.QUESTION_ID + " = ?");
			statements.add(s);
			s.setInt(1, questionId);
			rs = s.executeQuery();

			List<Vote> votes = new ArrayList<Vote>();
			while (rs.next()) {
				int voterId = rs.getInt(DerbyConfig.VOTER_ID);
				boolean voteTypeFlag = rs.getBoolean(DerbyConfig.VOTE_TYPE);
				VoteType voteType = voteTypeFlag ? VoteType.Up : VoteType.Down;
				votes.add(new Vote(voterId, voteType));
			}
			return votes;

		} catch (SQLException e) {
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}
	}

}
