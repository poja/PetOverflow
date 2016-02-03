package petoverflow.authentication;

/**
 * Validates data coming from the user.
 * 
 * TODO UnitTest
 */
public class DataValidator {

	private static final int MIN_USERNAME_LENGTH = 1;

	private static final int MIN_PASSWORD_LENGTH = 1;

	private static final char[] ALLOWED_PASS_SYMBOLS = { '!', '@', '#', '$', '%', '^', '&', '*' };

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

	private static boolean isLetter(char c) {
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
	}

	private static boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	private static boolean isUsernameChar(char c) {
		return isLetter(c) || isDigit(c);
	}

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
