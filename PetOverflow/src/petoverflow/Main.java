package petoverflow;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import petoverflow.dao.UserDao;
import petoverflow.dao.derby.DaoManagerDerby;
import petoverflow.dao.derby.DerbyUtils;

/**
 * This class is used to initiate multiple things.
 * 
 * It is a servlet. The <code>init</code> method is called on Tomcat startup,
 * using web.xml configuration.
 *
 */
public class Main implements ServletContextListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.ServletContextListener#contextInitialized(javax.servlet.
	 * ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent ev) {
		System.out.println("Initializing server");
		try {
			DaoManagerDerby.init();
			UserDao userDao = DaoManagerDerby.getInstance().getUserDao();
			if (Config.CREATE_INITIAL_DB && userDao.isEmpty()) {
				SampleDbInitiator.run();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.
	 * ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent ev) {
		System.out.println("Destroying server");
		DerbyUtils.shutdown();
	}

}
