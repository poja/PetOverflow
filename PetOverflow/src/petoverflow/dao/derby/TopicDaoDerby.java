package petoverflow.dao.derby;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import petoverflow.dao.TopicDao;

/**
 * The TopicDaoDerby class implements the TopicDao interface with Derby DB.
 */
public class TopicDaoDerby implements TopicDao {

	/**
	 * The single instance of this class
	 */
	private static TopicDaoDerby m_instance;

	/**
	 * Get the single instance of this class
	 * 
	 * @return the single instance of this class
	 */
	public static TopicDaoDerby getInstance() {
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
	public static void init() throws ClassNotFoundException, SQLException {
		System.out.println("Initiating topics database connection");
		DerbyUtils.initTable(DerbyConfig.TOPIC_TABLE_NAME, DerbyConfig.TOPIC_TABLE_CREATE);
		m_instance = new TopicDaoDerby();
	}

	/**
	 * Constructor (Private)
	 * 
	 * Used once to create the single instance of this class
	 */
	private TopicDaoDerby() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.TopicDao#getQuestionsByTopic(java.lang.String)
	 */
	@Override
	public List<Integer> getQuestionsByTopic(String topic) throws SQLException {
		Connection conn = DerbyUtils.getConnection(DerbyConfig.TOPIC_TABLE_CREATE);
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.QUESTION_ID + " FROM "
					+ DerbyConfig.TOPIC_TABLE_NAME + " WHERE " + DerbyConfig.TOPIC + " = ?");
			statements.add(s);
			s.setString(1, topic);
			rs = s.executeQuery();

			List<Integer> questionIds = new ArrayList<Integer>();
			while (rs.next()) {
				int id = rs.getInt(DerbyConfig.QUESTION_ID);
				questionIds.add(id);
			}
			return questionIds;

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
	 * @see petoverflow.dao.TopicDao#getQuestionTopics(int)
	 */
	@Override
	public List<String> getQuestionTopics(int questionId) throws SQLException {
		Connection conn = DerbyUtils.getConnection(DerbyConfig.TOPIC_TABLE_CREATE);
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.TOPIC + " FROM "
					+ DerbyConfig.TOPIC_TABLE_NAME + " WHERE " + DerbyConfig.QUESTION_ID + " = ?");
			statements.add(s);
			s.setInt(1, questionId);
			rs = s.executeQuery();

			List<String> relatedTopics = new ArrayList<String>();
			while (rs.next()) {
				String topic = rs.getString(DerbyConfig.TOPIC);
				relatedTopics.add(topic);
			}
			return relatedTopics;

		} catch (SQLException e) {
			DerbyUtils.printSQLException(e);
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}
	}

	public void setTopics(int questionId, List<String> topics) throws SQLException {
		Connection conn = DerbyUtils.getConnection(DerbyConfig.TOPIC_TABLE_CREATE);
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			for (String topic : topics) {
				PreparedStatement s = conn.prepareStatement("INSERT INTO " + DerbyConfig.TOPIC_TABLE_NAME + " ("
						+ DerbyConfig.QUESTION_ID + "," + DerbyConfig.TOPIC + ") VALUES (?, ?)");
				statements.add(s);
				s.setInt(1, questionId);
				s.setString(2, topic);
				rs = s.executeQuery();
			}

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
	 * @see petoverflow.dao.TopicDao#getAllTopics()
	 */
	public List<String> getAllTopics() throws SQLException {
		List<String> topics = new ArrayList<String>();

		Connection conn = DerbyUtils.getConnection(DerbyConfig.TOPIC_TABLE_CREATE);
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			PreparedStatement s = conn
					.prepareStatement("SELECT " + DerbyConfig.TOPIC + " FROM " + DerbyConfig.TOPIC_TABLE_NAME);
			rs = s.executeQuery();
			while (rs.next()) {
				String topic = rs.getString(DerbyConfig.TOPIC);
				if (!topics.contains(topic)) {
					topics.add(topic);
				}
			}

		} catch (SQLException e) {
			DerbyUtils.printSQLException(e);
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}

		return topics;
	}

}
