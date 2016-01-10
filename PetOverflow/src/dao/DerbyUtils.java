package dao;

import java.sql.SQLException;

public class DerbyUtils {

	
	/**
	 * Used after getting and exception in CREATE TABLE.
	 * 
	 * @param e Exception caused by an SQL statement
	 * @return Whether the exception is because there is already such a table.
	 */
	public static boolean tableAlreadyExists(SQLException e) {
		if (e.getSQLState().equals("X0Y32"))
			return true;
		else 
			return false;
	}
}
