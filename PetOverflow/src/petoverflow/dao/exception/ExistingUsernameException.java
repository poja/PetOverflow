package petoverflow.dao.exception;

/**
 * The ExistingUsernameException exception is thrown when trying to create a new
 * user with an existing username
 */
public class ExistingUsernameException extends Exception {

	private static final long serialVersionUID = 1L;

	public ExistingUsernameException() {
		super();
	}

	public ExistingUsernameException(String message) {
		super(message);
	}

}
