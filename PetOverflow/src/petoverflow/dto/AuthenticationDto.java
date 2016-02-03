package petoverflow.dto;

/**
 * A DTO for authentication
 * 
 * Is sent from the user with username and password, is sent back together with
 * a success boolean and an error message if there is one
 */
public class AuthenticationDto {

	private String m_username;

	private String m_password;

	private String m_message;

	private boolean m_success;

	private static String BAD_PASS_MESSAGE = "The username and password do not match.";

	/**
	 * Constructor
	 * 
	 * @param username
	 *            username of the user
	 * @param password
	 *            password of the user
	 * @param success
	 *            true if the authentication succeeded
	 */
	public AuthenticationDto(String username, String password, boolean success) {
		m_username = username;
		m_password = password;
		m_success = success;
		setSuccess(success);
	}

	/**
	 * Set the success flag of this AuthenticationDTO
	 * 
	 * @param success
	 *            new value for the success flag
	 */
	public void setSuccess(boolean success) {
		m_success = success;
		if (!success) {
			m_message = BAD_PASS_MESSAGE;
		}
	}

	/**
	 * Get the username this DTO holds
	 * 
	 * @return the username this DTO holds
	 */
	public String getUsername() {
		return m_username;
	}

	/**
	 * Get the password this DTO holds
	 * 
	 * @return the password this DTO holds
	 */
	public String getPassword() {
		return m_password;
	}

	/**
	 * Get the message this DTO holds
	 * 
	 * @return the message this DTO holds, null if empty
	 */
	public String getMessage() {
		return m_message;
	}

	/**
	 * Check if the authentication succeeded
	 * 
	 * @return true if the authentication succeeded, else - false
	 */
	public boolean getSuccess() {
		return m_success;
	}

}
