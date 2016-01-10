package dto;

/**
 * A DTO for authentication.
 * 
 * Is sent from the user with username and password, is sent back together with
 * a success boolean and an error message if there is one
 *
 */
public class AuthenticationDto {

	private String username;
	private String password;

	private boolean success;
	private String message;

	private static String BAD_PASS_MESSAGE = "The username and password do not match.";

	public void setSuccess(boolean success) {
		this.success = success;
		if (!success)
			this.message = BAD_PASS_MESSAGE;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public boolean getSuccess() {
		return success;
	}

	public String getMessage() {
		return message;
	}

	/**
	 * To be created when returning an AuthenitcationDto to the user.
	 *
	 * @param success
	 *            if the authentication succeeded
	 */
	public AuthenticationDto(String username, String password, boolean success) {
		this.username = username;
		this.password = password;
		this.success = success;
		this.setSuccess(success);
	}

}
