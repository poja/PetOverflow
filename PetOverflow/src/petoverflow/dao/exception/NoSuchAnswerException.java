package petoverflow.dao.exception;

/**
 * The NoSuchAnswerException exception is thrown when some data about an answer
 * is required but there is no such answer in the DB.
 */
public class NoSuchAnswerException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoSuchAnswerException() {
		super();
	}

	public NoSuchAnswerException(String message) {
		super(message);
	}

}
