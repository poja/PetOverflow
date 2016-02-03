package petoverflow.dao.exception;

public class DerbyNotInitialized extends IllegalStateException {

	private static final long serialVersionUID = 1L;

	public DerbyNotInitialized() {
		super();
	}

	public DerbyNotInitialized(String message) {
		super(message);
	}

}
