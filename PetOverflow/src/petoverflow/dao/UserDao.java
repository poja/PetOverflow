package petoverflow.dao;

import java.util.List;

import petoverflow.dao.items.Topic;
import petoverflow.dao.items.User;

/**
 * The UserDao interface provide a set of methods to manage, read and create new
 * users in a DB. All methods throws Exception in failure.
 */
public interface UserDao {

	/**
	 * Get user by it's id
	 * 
	 * @param userId
	 *            the user's id
	 * @return the user if exist
	 * @throws Exception
	 *             if fail
	 */
	public User getUser(int userId) throws Exception;

	/**
	 * Get user by it's username
	 * 
	 * @param username
	 *            the user's username
	 * @return the user if exist
	 * @throws Exception
	 *             if fail
	 */
	public User getUser(String username) throws Exception;

	/**
	 * Checks if a user exist by it's id
	 * 
	 * @param userId
	 *            the user's id
	 * @return true if the user exist, else - false
	 * @throws Exception
	 *             if fail
	 */
	public boolean exist(int userId) throws Exception;

	/**
	 * Check if a user exist by it's username
	 * 
	 * @param username
	 *            the user's username
	 * @return true if the user exist, else -false
	 * @throws Exception
	 *             if fail
	 */
	public boolean exist(String username) throws Exception;
	
	/**
	 * Check if there is any user in the DB.
	 * 
	 * @return true if there are no users, else false
	 * @throws Exception
	 */
	public boolean isEmpty() throws Exception;

	/**
	 * Create a new user
	 * 
	 * @param username
	 *            username of the user
	 * @param password
	 *            password of the user
	 * @param nickname
	 *            nickname of the user
	 * @param description
	 *            description of the user
	 * @param photoUrl
	 *            the user's photo URL
	 * @param phoneNum
	 *            the phone number of the user
	 * @return the user
	 * @throws Exception
	 *             if fail
	 */
	public User createUser(String username, String password, String nickname, String description, String photoUrl,
			String phoneNum, boolean wantsSms) throws Exception;

	/**
	 * Get a user's username
	 * 
	 * @param userId
	 *            the user's id
	 * @return the user's username
	 * @throws Exception
	 *             if fail
	 */
	public String getUserUsername(int userId) throws Exception;

	/**
	 * Get a user's nickname
	 * 
	 * @param userId
	 *            the user's id
	 * @return the user's nickname
	 * @throws Exception
	 *             if fail
	 */
	public String getUserNickname(int userId) throws Exception;

	/**
	 * Get a user's description
	 * 
	 * @param userId
	 *            the user's id
	 * @return the user's description
	 * @throws Exception
	 *             if fail
	 */
	public String getUserDescription(int userId) throws Exception;

	/**
	 * Get a user's photo URL
	 * 
	 * @param userId
	 *            the user's id
	 * @return the user's photo URL
	 * @throws Exception
	 *             if fail
	 */
	public String getUserPhotoURL(int userId) throws Exception;

	/**
	 * Get a user's phone number
	 * 
	 * @param userId
	 *            the user's id
	 * @return the user's phone number
	 * @throws Exception
	 *             if fail
	 */
	public String getUserPhoneNum(int userId) throws Exception;

	/**
	 * Check if this user wants SMS notifications.
	 * 
	 * @param userId
	 * @return true if the user wants SMS notifications, otherwise false
	 * @throws Exception
	 */
	public boolean getUserWantsSms(int userId) throws Exception;

	public List<Topic> getUserBestTopics(int userId, int size) throws Exception;

	/**
	 * Set a user's password to a new one
	 * 
	 * @param userId
	 *            the user's id
	 * @param password
	 *            new password
	 * @throws Exception
	 *             if fail
	 */
	public void setUserPassword(int userId, String password) throws Exception;

	/**
	 * Set a user's description to a new one
	 * 
	 * @param userId
	 *            the user's id
	 * @param description
	 *            new description
	 * @throws Exception
	 *             if fail
	 */
	public void setUserDescription(int userId, String description) throws Exception;

	/**
	 * Set a user's photo URL to a new one
	 * 
	 * @param userId
	 *            the user's id
	 * @param photoUrl
	 *            new photo URL
	 * @throws Exception
	 *             if fail
	 */
	public void setUserPhoto(int userId, String photoUrl) throws Exception;

	/**
	 * Set a user's phone number to a new one
	 * 
	 * @param userId
	 *            the user's id
	 * @param phoneNum
	 *            new phone number
	 * @throws Exception
	 *             if fail
	 */
	public void setUserPhoneNum(int userId, String phoneNum) throws Exception;

	/**
	 * Get a list of the users with the highest rating
	 * 
	 * @param size
	 *            the length of the requested list
	 * @param offset
	 *            offset in the total list
	 * @return a list with the most rated users
	 * @throws Exception
	 *             if fail
	 */
	public List<User> getMostRatedUsers(int size, int offset) throws Exception;

	/**
	 * Checks if a username and a password is authenticated
	 * 
	 * @param username
	 *            the user's username
	 * @param password
	 *            the user's password
	 * @return id of the user, if a user exists with this (username, password)
	 *         pair. Else - null.
	 * @throws Exception
	 *             if fail
	 */
	public Integer isAuthenticationPair(String username, String password) throws Exception;

}
