package petoverflow.dao.derby;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import petoverflow.dao.AnswerVoteDao;
import petoverflow.dao.DaoManager;
import petoverflow.dao.DaoObject;
import petoverflow.dao.items.Vote;
import petoverflow.dao.items.Vote.VoteType;

/**
 * The AnswerVoteDaoDerby class implements the AnswerVoteDao interface with
 * derby data base.
 */
public class AnswerVoteDaoDerby extends DaoObject implements AnswerVoteDao {

	/**
	 * The single instance of this class
	 */
	private static AnswerVoteDaoDerby m_instance;

	/**
	 * Get the single instance of this class
	 * 
	 * @return the single instance of this class
	 */
	public static AnswerVoteDaoDerby getInstance() {
		if (m_instance == null) {
			throw new IllegalStateException("Answers votes dao wasn't initialized.");
		}
		return m_instance;
	}

	/**
	 * Initialize this derby DAO
	 * 
	 * @throws ClassNotFoundException
	 *             if derby is not installed
	 * @throws SQLException
	 *             if failed to created this DAO tables
	 */
	public static void init(DaoManager daoManager) throws ClassNotFoundException, SQLException {
		System.out.println("Initiating answers votes database connection");
		DerbyUtils.initTable(DerbyConfig.DB_NAME, DerbyConfig.ANSWER_VOTE_TABLE_CREATE);
		m_instance = new AnswerVoteDaoDerby(daoManager);
	}

	/**
	 * Constructor (Private)
	 * 
	 * Used once to create the single instance of this DAO
	 */
	private AnswerVoteDaoDerby(DaoManager daoManager) {
		super(daoManager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.AnswerVoteDao#addVote(int, petoverflow.dao.Vote)
	 */
	public void addVote(int answerId, Vote vote) throws SQLException {
		// Remove previous vote
		removeVote(answerId, vote.getVoterId());

		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn
					.prepareStatement("INSERT INTO " + DerbyConfig.ANSWER_VOTE_TABLE_NAME + " (" + DerbyConfig.VOTER_ID
							+ "," + DerbyConfig.ANSWER_ID + "," + DerbyConfig.VOTE_TYPE + ") VALUES (?, ?, ?)");
			statements.add(s);
			s.setInt(1, vote.getVoterId());
			s.setInt(2, answerId);
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
	 * @see petoverflow.dao.AnswerVoteDao#removeVote(int, int)
	 */
	public void removeVote(int answerId, int voterId) throws SQLException {
		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement("DELETE FROM " + DerbyConfig.ANSWER_VOTE_TABLE_NAME + " WHERE "
					+ DerbyConfig.VOTER_ID + " = ? AND " + DerbyConfig.ANSWER_ID + " = ?");
			statements.add(s);
			s.setInt(1, voterId);
			s.setInt(2, answerId);
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
	 * @see petoverflow.dao.AnswerVoteDao#getAnswerVotes(int)
	 */
	@Override
	public List<Vote> getAnswerVotes(int answerId) throws SQLException {
		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement(
					"SELECT * FROM " + DerbyConfig.ANSWER_VOTE_TABLE_NAME + " WHERE " + DerbyConfig.ANSWER_ID + " = ?");
			statements.add(s);
			s.setInt(1, answerId);
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
