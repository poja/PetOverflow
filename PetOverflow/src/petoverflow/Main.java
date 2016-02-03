package petoverflow;

import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import petoverflow.dao.derby.DerbyUtils;

/**
 * This class is used to initiate multiple things.
 * 
 * It is a servlet. The <code>init</code> method is called on Tomcat startup,
 * using web.xml configuration.
 *
 */
public class Main implements ServletContextListener {

	/**
	 * Initialization of the server
	 */
	@Override
	public void contextInitialized(ServletContextEvent ev) {
		System.out.println("Initializing server");
		try {
			DerbyUtils.init();

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Destruction of the server
	 */
	@Override
	public void contextDestroyed(ServletContextEvent ev) {
		System.out.println("Destroying server");
		DerbyUtils.shutdown();
	}

	/**
	 * Used for debugging purposes only
	 */
	public static void main(String[] args) {
	}

}
