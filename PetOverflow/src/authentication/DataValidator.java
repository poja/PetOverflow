package authentication;

/**
 * Validates data coming from the user.
 * 
 * TODO UnitTest
 */
public class DataValidator {
	
	public static boolean isValidUsername(String username) {
		for (int i = 0; i < username.length(); i++)
			if (!isUsernameChar(username.charAt(i)))
				return false;
		return true;
	}
	
	public static boolean isValidPassword(String password) {
		for (int i = 0; i < password.length(); i++)
			if (!isPasswordChar(password.charAt(i)))
				return false;
		return true;
	}
	
	private static boolean isLetter(char c) {
		return c >= 'A' && c <= 'z';
	}
	
	private static boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}
	
	private static boolean isUsernameChar(char c) {
		return isLetter(c) || isDigit(c);
	}
	
	private static char[] ALLOWED_PASS_SYMBOLS = {'!', '@', '#', '$', '%', '^', '&', '*'};
	private static boolean isPasswordChar(char c) {

		if (isLetter(c) || isDigit(c))
			return true;
		
		
		for (char sym : ALLOWED_PASS_SYMBOLS) {
			if (c == sym) return true;
		}
		
		return false;
		
	}

}
