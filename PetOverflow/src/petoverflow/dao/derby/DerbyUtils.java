package petoverflow.dao.derby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DerbyUtils {

	/**
	 * Initialize all derby DAOs
	 * 
	 * @throws ClassNotFoundException
	 *             if derby is not installed
	 * @throws SQLException
	 *             if failed to created tables
	 */
	public static void init() throws ClassNotFoundException, SQLException {
		TopicDaoDerby.init();
		QuestionVoteDaoDerby.init();
		AnswerVoteDaoDerby.init();
		QuestionDaoDerby.init(QuestionVoteDaoDerby.getInstance(), TopicDaoDerby.getInstance());
		AnswerDaoDerby.init(AnswerVoteDaoDerby.getInstance());
		UserDaoDerby.init(QuestionDaoDerby.getInstance(), AnswerDaoDerby.getInstance());
	}

	/**
	 * Initialize a derby table
	 * 
	 * @param tableName
	 *            the name of the table
	 * @param tableCreate
	 *            the CREATE TABLE command
	 * @throws ClassNotFoundException
	 *             if derby is not installed
	 * @throws SQLException
	 *             if derby fail
	 */
	public static void initTable(String tableName, String tableCreate) throws ClassNotFoundException, SQLException {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw e;
		}

		try {
			Connection conn = DerbyUtils.getConnection(tableName);
			Statement s = conn.createStatement();
			s.execute(tableCreate);
			s.close();
			conn.close();
		} catch (SQLException e) {
			if (DerbyUtils.tableAlreadyExists(e)) {
				System.out.println("Using an existing table");
			} else {
				e.printStackTrace();
				throw e;
			}
		}
	}

	/**
	 * Shuts down the Derby DB
	 * 
	 * In embedded mode, an application should shut down the database. If the
	 * application fails to shut down the database, Derby will not perform a
	 * checkpoint when the JVM shuts down. This means that it will take longer
	 * to boot (connect to) the database the next time, because Derby needs to
	 * perform a recovery operation.
	 *
	 * It is also possible to shut down the Derby system/engine, which
	 * automatically shuts down all booted databases.
	 *
	 * Explicitly shutting down the database or the Derby engine with the
	 * connection URL is preferred. This style of shutdown will always throw an
	 * SQLException.
	 *
	 * Not shutting down when in a client environment, see method Javadoc.
	 */
	public static void shutdown() {
		try {
			// the shutdown=true attribute shuts down Derby
			DriverManager.getConnection("jdbc:derby:;shutdown=true");

			// To shut down a specific database only, but keep the
			// engine running (for example for connecting to other
			// databases), specify a database in the connection URL:
			// DriverManager.getConnection("jdbc:derby:" + dbName +
			// ";shutdown=true");
		} catch (SQLException se) {
			if (((se.getErrorCode() == 50000) && ("XJ015".equals(se.getSQLState())))) {
				// we got the expected exception
				System.out.println("Derby shut down normally");
				// Note that for single database shutdown, the expected
				// SQL state is "08006", and the error code is 45000.
			} else {
				// if the error code or SQLState is different, we have
				// an unexpected exception (shutdown failed)
				System.err.println("Derby did not shut down normally");
				printSQLException(se);
			}
		}
	}

	/**
	 * Prints details of an SQLException chain to <code>System.err</code>.
	 * Details included are SQL State, Error code, Exception message.
	 *
	 * @param e
	 *            the SQLException from which to print details.
	 */
	private static void printSQLException(SQLException e) {
		// Unwraps the entire exception chain to unveil the real cause of the
		// Exception.
		while (e != null) {
			System.err.println("\n----- SQLException -----");
			System.err.println("  SQL State:  " + e.getSQLState());
			System.err.println("  Error Code: " + e.getErrorCode());
			System.err.println("  Message:    " + e.getMessage());
			// for stack traces, refer to derby.log or uncomment this:
			e.printStackTrace(System.err);
			e = e.getNextException();
		}
	}

	/**
	 * Reports a data verification failure to System.err with the given message.
	 *
	 * @param message
	 *            A message describing what failed.
	 */
	public static void reportFailure(String message) {
		System.err.println("\nData verification failed:");
		System.err.println('\t' + message);
	}

	/**
	 * Cleans up the database connection
	 * 
	 * Release all open resources to avoid unnecessary memory usage
	 * 
	 * @param rs
	 *            ResultSet
	 * @param s
	 *            Derby statements
	 * @param conn
	 *            The connection to the database
	 */
	public static void cleanUp(ResultSet rs, List<Statement> statements, Connection conn) {
		// ResultSet
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
		} catch (SQLException sqle) {
			printSQLException(sqle);
		}

		// Statements and PreparedStatements
		int i = 0;
		while (!statements.isEmpty()) {
			// PreparedStatement extend Statement
			Statement st = (Statement) statements.remove(i);
			try {
				if (st != null) {
					st.close();
					st = null;
				}
			} catch (SQLException sqle) {
				printSQLException(sqle);
			}
		}

		// Connection
		try {
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (SQLException sqle) {
			printSQLException(sqle);
		}
	}

	/**
	 * Get a connection to a DB
	 * 
	 * @param dbName
	 *            name of the DB
	 * @return a connection object
	 * @throws SQLException
	 *             if fail
	 */
	public static Connection getConnection(String dbName) throws SQLException {
		try {
			return DriverManager.getConnection("jdbc:derby:" + dbName + ";create=true");
		} catch (SQLException e) {
			printSQLException(e);
			throw e;
		}
	}

	/**
	 * Used after getting and exception in CREATE TABLE.
	 * 
	 * @param e
	 *            Exception caused by an SQL statement
	 * @return Whether the exception is because there is already such a table.
	 */
	public static boolean tableAlreadyExists(SQLException e) {
		if (e.getSQLState().equals("X0Y32")) {
			return true;
		} else {
			return false;
		}
	}

}
