package _main;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import dao.UserDaoDerby;

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
		UserDaoDerby.init();
	}
	
	/**
	 * Destruction of the server
	 */
	@Override
	public void contextDestroyed(ServletContextEvent ev) {
		System.out.println("Destroying server");
		UserDaoDerby.shutdown();
	}

	/**
	 * Used for debugging purposes only
	 */
	public static void main(String[] args) {
	}

}
