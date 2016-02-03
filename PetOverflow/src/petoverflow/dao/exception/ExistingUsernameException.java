package petoverflow.dao.exception;

public class ExistingUsernameException extends Exception {

	private static final long serialVersionUID = 1L;

	public ExistingUsernameException() {
		super();
	}

	public ExistingUsernameException(String message) {
		super(message);
	}

}
