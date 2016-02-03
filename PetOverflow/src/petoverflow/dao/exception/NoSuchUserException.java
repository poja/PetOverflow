package petoverflow.dao.exception;

/**
 * The UserDoesnotExistException is thrown if a user is requested from a DAO and
 * it's not existing.
 */
public class NoSuchUserException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoSuchUserException() {
		super();
	}

	public NoSuchUserException(String message) {
		super(message);
	}

}
