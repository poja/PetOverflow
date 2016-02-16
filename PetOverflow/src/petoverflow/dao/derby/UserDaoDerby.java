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
import petoverflow.dao.UserDao;
import petoverflow.dao.items.User;
import petoverflow.dao.utility.exception.ExistingUsernameException;
import petoverflow.dao.utility.exception.NoSuchUserException;

/**
 * The UserDaoDerby class implements the UserDao with the Derby data base.
 */
public class UserDaoDerby extends DaoObject implements UserDao {

	/**
	 * The single instance of this class
	 */
	private static UserDaoDerby m_instance;

	/**
	 * Get the single instance of this class
	 * 
	 * @return the single instance of this class
	 */
	public static UserDaoDerby getInstance() {
		if (m_instance == null) {
			throw new IllegalStateException("Users dao wasn't initialized.");
		}
		return m_instance;
	}

	/**
	 * Initialize this derby DAO
	 * 
	 * @param questionDao
	 *            The questions DAO this DAO's users are related to
	 * @param answerDao
	 *            The answers DAO this DAO's users are related to
	 * @throws ClassNotFoundException
	 *             if derby is not installed
	 * @throws SQLException
	 *             if failed to created this DAO tables
	 */
	public static void init(DaoManager daoManager) throws ClassNotFoundException, SQLException {
		System.out.println("Initiating users database connection");
		DerbyUtils.initTable(DerbyConfig.DB_NAME, DerbyConfig.USER_TABLE_CREATE);
		m_instance = new UserDaoDerby(daoManager);
	}

	/**
	 * Constructor (Private)
	 * 
	 * Used once to create the single instance
	 * 
	 * @param questionDao
	 *            The questions DAO this DAO's users are related to
	 * @param answerDao
	 *            The answers DAO this DAO's users are related to
	 */
	private UserDaoDerby(DaoManager daoManager) {
		super(daoManager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.UserDao#getUser(int)
	 */
	@Override
	public User getUser(int userId) throws SQLException, NoSuchUserException {
		if (!exist(userId)) {
			throw new NoSuchUserException();
		}
		return new User(m_daoManager, userId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.UserDao#getUser(java.lang.String)
	 */
	@Override
	public User getUser(String username) throws SQLException, NoSuchUserException {
		if (!exist(username)) {
			throw new NoSuchUserException();
		}

		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.ID + " FROM "
					+ DerbyConfig.USER_TABLE_NAME + " WHERE " + DerbyConfig.USERNAME + " = ?");
			statements.add(s);
			s.setString(1, username);
			rs = s.executeQuery();
			if (!rs.next()) {
				throw new SQLException("Unexpected error");
			}
			int id = rs.getInt(DerbyConfig.ID);
			return new User(m_daoManager, id);

		} catch (SQLException e) {
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.UserDao#exist(int)
	 */
	@Override
	public boolean exist(int userId) throws SQLException {
		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement(
					"SELECT * FROM " + DerbyConfig.USER_TABLE_NAME + " WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setInt(1, userId);
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
	 * @see petoverflow.dao.UserDao#exist(java.lang.String)
	 */
	@Override
	public boolean exist(String username) throws SQLException {
		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement(
					"SELECT * FROM " + DerbyConfig.USER_TABLE_NAME + " WHERE " + DerbyConfig.USERNAME + " = ?");
			statements.add(s);
			s.setString(1, username);
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
	 * @see petoverflow.dao.UserDao#createUser(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public User createUser(String username, String password, String nickname, String description, String photoUrl,
			String phoneNum) throws SQLException, NoSuchUserException, ExistingUsernameException {
		if (exist(username)) {
			throw new ExistingUsernameException();
		}
		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement(
					"INSERT INTO " + DerbyConfig.USER_TABLE_NAME + " (" + DerbyConfig.USERNAME + ", "
							+ DerbyConfig.PASSWORD + ", " + DerbyConfig.NICKNAME + ", " + DerbyConfig.DESCRIPTION + ", "
							+ DerbyConfig.PHOTO_URL + ", " + DerbyConfig.PHONE_NUM + ") VALUES (?, ?, ?, ?, ?, ?)",
					new String[] { DerbyConfig.ID });
			statements.add(s);
			s.setString(1, username);
			s.setString(2, password);
			s.setString(3, nickname);
			s.setString(4, description);
			s.setString(5, photoUrl);
			s.setString(6, phoneNum);
			s.executeUpdate();
			rs = s.getGeneratedKeys();

			if (!rs.next()) {
				throw new SQLException("Unexpected error");
			}
			int id = rs.getInt(1);
			return new User(m_daoManager, id);

		} catch (SQLException e) {
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.UserDao#getUserUsername(int)
	 */
	@Override
	public String getUserUsername(int userId) throws SQLException, NoSuchUserException {
		if (!exist(userId)) {
			throw new NoSuchUserException();
		}

		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.USERNAME + " FROM "
					+ DerbyConfig.USER_TABLE_NAME + " WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setInt(1, userId);
			rs = s.executeQuery();

			if (!rs.next()) {
				throw new SQLException("Unexpected error");
			}
			return rs.getString(DerbyConfig.USERNAME);

		} catch (SQLException e) {
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.UserDao#getUserNickname(int)
	 */
	@Override
	public String getUserNickname(int userId) throws SQLException, NoSuchUserException {
		if (!exist(userId)) {
			throw new NoSuchUserException();
		}

		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.NICKNAME + " FROM "
					+ DerbyConfig.USER_TABLE_NAME + " WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setInt(1, userId);
			rs = s.executeQuery();

			if (!rs.next()) {
				throw new SQLException("Unexpected error");
			}
			return rs.getString(DerbyConfig.NICKNAME);

		} catch (SQLException e) {
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.UserDao#getUserDescription(int)
	 */
	@Override
	public String getUserDescription(int userId) throws SQLException, NoSuchUserException {
		if (!exist(userId)) {
			throw new NoSuchUserException();
		}

		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.DESCRIPTION + " FROM "
					+ DerbyConfig.USER_TABLE_NAME + " WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setInt(1, userId);
			rs = s.executeQuery();

			if (!rs.next()) {
				throw new SQLException("Unexpected error");
			}
			return rs.getString(DerbyConfig.DESCRIPTION);

		} catch (SQLException e) {
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.UserDao#getUserPhotoURL(int)
	 */
	@Override
	public String getUserPhotoURL(int userId) throws SQLException, NoSuchUserException {
		if (!exist(userId)) {
			throw new NoSuchUserException();
		}

		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.PHOTO_URL + " FROM "
					+ DerbyConfig.USER_TABLE_NAME + " WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setInt(1, userId);
			rs = s.executeQuery();

			if (!rs.next()) {
				throw new SQLException("Unexpected error");
			}
			return rs.getString(DerbyConfig.PHOTO_URL);

		} catch (SQLException e) {
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.UserDao#getUserPhoneNum(int)
	 */
	@Override
	public String getUserPhoneNum(int userId) throws SQLException, NoSuchUserException {
		if (!exist(userId)) {
			throw new NoSuchUserException();
		}

		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.PHONE_NUM + " FROM "
					+ DerbyConfig.USER_TABLE_NAME + " WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setInt(1, userId);
			rs = s.executeQuery();

			if (!rs.next()) {
				throw new SQLException("Unexpected error");
			}
			return rs.getString(DerbyConfig.PHONE_NUM);

		} catch (SQLException e) {
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.UserDao#setUserPassword(int, java.lang.String)
	 */
	@Override
	public void setUserPassword(int userId, String password) throws SQLException, NoSuchUserException {
		if (!exist(userId)) {
			throw new NoSuchUserException();
		}

		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement("UPDATE " + DerbyConfig.USER_TABLE_NAME + " SET "
					+ DerbyConfig.PASSWORD + " = ? WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setString(1, password);
			s.setInt(2, userId);
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
	 * @see petoverflow.dao.UserDao#setUserDescription(int, java.lang.String)
	 */
	@Override
	public void setUserDescription(int userId, String description) throws SQLException, NoSuchUserException {
		if (!exist(userId)) {
			throw new NoSuchUserException();
		}

		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement("UPDATE " + DerbyConfig.USER_TABLE_NAME + " SET "
					+ DerbyConfig.DESCRIPTION + " = ? WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setString(1, description);
			s.setInt(2, userId);
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
	 * @see petoverflow.dao.UserDao#setUserPhoto(int, java.lang.String)
	 */
	@Override
	public void setUserPhoto(int userId, String photoUrl) throws SQLException, NoSuchUserException {
		if (!exist(userId)) {
			throw new NoSuchUserException();
		}

		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement("UPDATE " + DerbyConfig.USER_TABLE_NAME + " SET "
					+ DerbyConfig.PHOTO_URL + " = ? WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setString(1, photoUrl);
			s.setInt(2, userId);
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
	 * @see petoverflow.dao.UserDao#setUserPhoneNum(int, java.lang.String)
	 */
	@Override
	public void setUserPhoneNum(int userId, String phoneNum) throws SQLException, NoSuchUserException {
		if (!exist(userId)) {
			throw new NoSuchUserException();
		}

		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement("UPDATE " + DerbyConfig.USER_TABLE_NAME + " SET "
					+ DerbyConfig.PHONE_NUM + " = ? WHERE " + DerbyConfig.ID + " = ?");
			statements.add(s);
			s.setString(1, phoneNum);
			s.setInt(2, userId);
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
	 * @see petoverflow.dao.UserDao#getMostRatedUsers(int, int)
	 */
	public List<User> getMostRatedUsers(int size, int offset) throws SQLException {
		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		List<User> allUsers = new ArrayList<User>();
		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn
					.prepareStatement("SELECT " + DerbyConfig.ID + " FROM " + DerbyConfig.USER_TABLE_NAME);
			statements.add(s);
			rs = s.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(DerbyConfig.ID);
				allUsers.add(new User(m_daoManager, id));
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
		}

		Utility.sortByRating(allUsers);
		return Utility.cutList(allUsers, size, offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see petoverflow.dao.UserDao#isAuthenticationPair(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public boolean isAuthenticationPair(String username, String password) throws SQLException {
		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(DerbyConfig.DB_NAME);
			PreparedStatement s = conn.prepareStatement("SELECT " + DerbyConfig.PASSWORD + " FROM "
					+ DerbyConfig.USER_TABLE_NAME + " WHERE " + DerbyConfig.USERNAME + " = ?");
			statements.add(s);
			s.setString(1, username);
			rs = s.executeQuery();

			if (!rs.next()) {
				DerbyUtils.reportFailure("No such user");
				return false;
			} else if (rs.getString(DerbyConfig.PASSWORD).equals(password)) {
				return true;
			} else {
				DerbyUtils.reportFailure("User and password do not match");
				return false;
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			// release all open resources to avoid unnecessary memory usage
			DerbyUtils.cleanUp(rs, statements, conn);
		}
	}

}
