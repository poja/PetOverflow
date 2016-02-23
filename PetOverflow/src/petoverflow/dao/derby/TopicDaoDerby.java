package petoverflow.dao.derby;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import petoverflow.Utility;
import petoverflow.dao.DaoManager;
import petoverflow.dao.DaoObject;
import petoverflow.dao.TopicDao;
import petoverflow.dao.items.Question;
import petoverflow.dao.items.Topic;

/**
 * The TopicDaoDerby class implements the TopicDao interface with Derby DB.
 */
public class TopicDaoDerby extends DaoObject implements TopicDao {

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
		if (m_instance == null) {
			throw new IllegalStateException("Topics dao wasn't initialized.");
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
		System.out.println("Initiating topics database connection");
		DerbyUtils.initTable(DerbyConfig.DB_NAME, DerbyConfig.TOPIC_TABLE_CREATE);
		m_instance = new TopicDaoDerby(daoManager);
	}

	/**
	 * Constructor (Private)
	 * 
	 * Used once to create the single instance of this class
	 */
	private TopicDaoDerby(DaoManager daoManager) {
		super(daoManager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.TopicDao#getQuestionsByTopic(java.lang.String)
	 */
	@Override
	public List<Question> getQuestionsByTopic(String topic, int size, int offset) throws SQLException {
		List<Question> questions = getQuestionsByTopicAll(topic);
		return Utility.cutList(questions, size, offset);
	}

	public List<Question> getQuestionsByTopicAll(String topic) throws SQLException {
		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.QUESTION_ID + " FROM "
					+ DerbyConfig.TOPIC_TABLE_NAME + " WHERE " + DerbyConfig.TOPIC + " = ?");
			statements.add(s);
			s.setString(1, topic);
			rs = s.executeQuery();

			List<Question> questions = new ArrayList<Question>();
			while (rs.next()) {
				int questionId = rs.getInt(DerbyConfig.QUESTION_ID);
				try {
					Question question = getDaoManager().getQuestionDao().getQuestion(questionId);
					questions.add(question);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return questions;

		} catch (SQLException e) {
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
	public List<Topic> getQuestionTopics(int questionId) throws SQLException {
		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.TOPIC + " FROM "
					+ DerbyConfig.TOPIC_TABLE_NAME + " WHERE " + DerbyConfig.QUESTION_ID + " = ?");
			statements.add(s);
			s.setInt(1, questionId);
			rs = s.executeQuery();

			List<Topic> relatedTopics = new ArrayList<Topic>();
			while (rs.next()) {
				String topicName = rs.getString(DerbyConfig.TOPIC);
				Topic topic = new Topic(getDaoManager(), topicName);
				relatedTopics.add(topic);
			}
			return relatedTopics;

		} catch (SQLException e) {
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.TopicDao#getTopicRating(java.lang.String)
	 */
	@Override
	public double getTopicRating(String topic) throws Exception {
		List<Question> questions = getDaoManager().getTopicDao().getQuestionsByTopicAll(topic);
		double rating = 0;
		for (Question question : questions) {
			rating += question.getRating();
		}
		return rating;
	}

	public void setQuestionTopics(int questionId, List<String> topics) throws SQLException {
		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			for (String topic : topics) {
				PreparedStatement s = conn.prepareStatement("INSERT INTO " + DerbyConfig.TOPIC_TABLE_NAME + " ("
						+ DerbyConfig.QUESTION_ID + "," + DerbyConfig.TOPIC + ") VALUES (?, ?)");
				statements.add(s);
				s.setInt(1, questionId);
				s.setString(2, topic);
				s.executeUpdate();
			}

		} catch (SQLException e) {
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
	public List<Topic> getAllTopics() throws SQLException {
		List<String> topicsNames = new ArrayList<String>();
		List<Topic> topics = new ArrayList<Topic>();

		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn
					.prepareStatement("SELECT " + DerbyConfig.TOPIC + " FROM " + DerbyConfig.TOPIC_TABLE_NAME);
			rs = s.executeQuery();

			while (rs.next()) {
				String topicName = rs.getString(DerbyConfig.TOPIC);
				if (!topicsNames.contains(topicName)) {
					topicsNames.add(topicName);
					topics.add(new Topic(getDaoManager(), topicName));
				}
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}

		return topics;
	}

	@Override
	public List<Topic> getPopularTopics(int size, int offset) throws SQLException {
		List<Topic> allTopics = getAllTopics();
		Utility.sortByRating(allTopics);
		return Utility.cutList(allTopics, size, offset);
	}

	@Override
	public List<Question> getBestQuestionsByTopic(String topic, int size, int offset) throws Exception {
		List<Question> questions = getQuestionsByTopicAll(topic);
		Utility.sortByRating(questions);
		return Utility.cutList(questions, size, offset);
	}

}
