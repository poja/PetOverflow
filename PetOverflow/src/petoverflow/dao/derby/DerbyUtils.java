package petoverflow.dao.derby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Tools for manipulation Derby DB
 *
 */
public class DerbyUtils {

	/**
	 * Initialize a derby table
	 * 
	 * @param dbName
	 *            the name of the data base
	 * @param tableCreate
	 *            the CREATE TABLE command
	 * @throws ClassNotFoundException
	 *             if derby is not installed
	 * @throws SQLException
	 *             if derby fail
	 */
	public static void initTable(String dbName, String tableCreate) throws ClassNotFoundException, SQLException {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw e;
		}

		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>();
		ResultSet rs = null;

		try {
			conn = DerbyUtils.getConnection(dbName);
			Statement s = conn.createStatement();
			statements.add(s);
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
		} finally {
			DerbyUtils.cleanUp(rs, statements, conn);
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
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException sqle) {
				printSQLException(sqle);
			}
		}

		// Statements and PreparedStatements
		int i = statements.size() - 1;
		while (!statements.isEmpty()) {
			// PreparedStatement extend Statement
			Statement st = (Statement) statements.remove(i);
			if (st != null) {
				try {
					st.close();
				} catch (SQLException sqle) {
					printSQLException(sqle);
				}
			}
			i--;
		}

		// Connection
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException sqle) {
				printSQLException(sqle);
			}
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
			// printSQLException(e);
			// throw e;
			e.printStackTrace();
			return null;
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
