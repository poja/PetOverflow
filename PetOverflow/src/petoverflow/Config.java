package petoverflow;

/**
 * This class is used to configure different aspects of the server. It holds
 * static variables, which can be changed in order to change the behavior of the
 * server;
 */
public class Config {

	/**
	 * When the server is initialized, before there is a server, there are no
	 * users. Enabling CREATE_INITIAL_DB, causes the server to inject sample
	 * data, using the class SampleDbInitiaor.
	 * 
	 * @see petoverflow.SampleDbInitiaor
	 */
	public static final boolean CREATE_INITIAL_DB = true;

}
