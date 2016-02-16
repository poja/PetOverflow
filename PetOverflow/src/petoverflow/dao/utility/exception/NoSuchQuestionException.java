package petoverflow.dao.utility.exception;

/**
 * The NoSuchAnswerException exception is thrown when some data about a question
 * is required but there is no such question in the DB.
 */
public class NoSuchQuestionException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoSuchQuestionException() {
		super();
	}

	public NoSuchQuestionException(String message) {
		super(message);
	}

}
