package petoverflow.dao.exception;

/**
 * The DerbyNotInitialized exception is thrown when trying to use derby DB when
 * it hasn't initialized yet.
 */
public class DerbyNotInitialized extends IllegalStateException {

	private static final long serialVersionUID = 1L;

	public DerbyNotInitialized() {
		super();
	}

	public DerbyNotInitialized(String message) {
		super(message);
	}

}
