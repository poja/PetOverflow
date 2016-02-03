package petoverflow.dao.exception;

public class NoSuchQuestionException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoSuchQuestionException() {
		super();
	}

	public NoSuchQuestionException(String message) {
		super(message);
	}

}
