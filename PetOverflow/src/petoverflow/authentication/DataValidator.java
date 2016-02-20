package petoverflow.authentication;

/**
 * Validates data coming from the user.
 */
public class DataValidator {

	/**
	 * Minimum username length
	 */
	private static final int MIN_USERNAME_LENGTH = 1;

	/**
	 * Minimum password length
	 */
	private static final int MIN_PASSWORD_LENGTH = 1;

	/**
	 * Allowed password symbols
	 */
	private static final char[] ALLOWED_PASS_SYMBOLS = { '!', '@', '#', '$', '%', '^', '&', '*' };

	/**
	 * Checks if a username is valid
	 * 
	 * @param username
	 *            the username that being checked
	 * @return true if username is valid, else- false
	 */
	public static boolean isValidUsername(String username) {
		if (username == null) {
			return false;
		} else if (username.length() < MIN_USERNAME_LENGTH) {
			return false;
		}
		for (int i = 0; i < username.length(); i++) {
			if (!isUsernameChar(username.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if a password is valid
	 * 
	 * @param password
	 *            the password that being checked
	 * @return true if the password is valid, else -false
	 */
	public static boolean isValidPassword(String password) {
		if (password == null) {
			return false;
		} else if (password.length() < MIN_PASSWORD_LENGTH) {
			return false;
		}
		for (int i = 0; i < password.length(); i++) {
			if (!isPasswordChar(password.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if a char is a letter
	 * 
	 * @param c
	 *            the char that being checked
	 * @return true if the char is a letter, else -false
	 */
	private static boolean isLetter(char c) {
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
	}

	/**
	 * Checks if a char is a digit
	 * 
	 * @param c
	 *            the char that being checked
	 * @return true if the char is a digit, else false
	 */
	private static boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	/**
	 * Checks if a char is allowed in a username
	 * 
	 * @param c
	 *            the char that being checked
	 * @return true if the char is allowed, else -false
	 */
	private static boolean isUsernameChar(char c) {
		return isLetter(c) || isDigit(c);
	}

	/**
	 * Checks if a char is allowed in a password
	 * 
	 * @param c
	 *            the char that being checked
	 * @return true if the char is allowed, else -false
	 */
	private static boolean isPasswordChar(char c) {
		if (isLetter(c) || isDigit(c)) {
			return true;
		}

		for (char sym : ALLOWED_PASS_SYMBOLS) {
			if (c == sym) {
				return true;
			}
		}

		return false;
	}

}
