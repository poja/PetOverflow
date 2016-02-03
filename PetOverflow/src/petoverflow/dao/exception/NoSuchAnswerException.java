package petoverflow.dao.exception;

public class NoSuchAnswerException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoSuchAnswerException() {
		super();
	}

	public NoSuchAnswerException(String message) {
		super(message);
	}

}
