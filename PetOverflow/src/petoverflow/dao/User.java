package petoverflow.dao;

import java.util.List;

/**
 * The User class represent a user in the PetOverflow system. This class uses
 * DAO to get it's data and in most of it's methods exceptions are thrown if the
 * DAO fails.
 * 
 * @see UserDao
 * @see QuestionDao
 * @see AnswerDao
 */
public class User {

	/**
	 * Id of this user
	 */
	private final int m_id;

	/**
	 * The DAO of this user, holds all the user data
	 */
	private final UserDao m_userDao;

	/**
	 * The question DAO that holds the user submitted questions
	 */
	private final QuestionDao m_questionDao;

	/**
	 * The answer DAO that holds the user submitted answers
	 */
	private final AnswerDao m_answerDao;

	/**
	 * Constructor
	 * 
	 * This constructor is not meant to be used directly. It should be called
	 * through an users DAO
	 * 
	 * @param id
	 *            id of the user
	 * @param userDao
	 *            the users DAO of this user
	 * @param questionDao
	 *            the questions DAO of this user
	 * @param answerDao
	 *            the answers DAO of this user
	 */
	public User(int id, UserDao userDao, QuestionDao questionDao, AnswerDao answerDao) {
		m_id = id;
		m_userDao = userDao;
		m_questionDao = questionDao;
		m_answerDao = answerDao;
	}

	/**
	 * Get the id of the user
	 * 
	 * @return this user's id
	 */
	public int getId() {
		return m_id;
	}

	/**
	 * Get the username of this user
	 * 
	 * @return this user's username
	 * @throws Exception
	 *             if DAO fails
	 */
	public String getUsername() throws Exception {
		return m_userDao.getUserUsername(m_id);
	}

	/**
	 * Get the nickname of this user
	 * 
	 * @return this user's nickname
	 * @throws Exception
	 *             if DAO fails
	 */
	public String getNickname() throws Exception {
		return m_userDao.getUserNickname(m_id);
	}

	/**
	 * Get the description of this user
	 * 
	 * @return this user' description
	 * @throws Exception
	 *             if DAO fails
	 */
	public String getDescription() throws Exception {
		return m_userDao.getUserDescription(m_id);
	}

	/**
	 * Get the URL of the photo of this user
	 * 
	 * @return this user's photo URL
	 * @throws Exception
	 *             if DAO fails
	 */
	public String getPhotoUrl() throws Exception {
		return m_userDao.getUserPhotoURL(m_id);
	}

	/**
	 * Get the phone number of this user
	 * 
	 * @return this user's phone number
	 * @throws Exception
	 *             if DAO fails
	 */
	public String getPhoneNum() throws Exception {
		return m_userDao.getUserPhoneNum(m_id);
	}

	/**
	 * Get the rating of this user
	 * 
	 * @return this user's rating
	 * @throws Exception
	 *             if DAO fails
	 */
	public double getRating() throws Exception {
		List<Question> userQuestions = m_questionDao.getQuestionsByAuthor(m_id);
		List<Answer> userAnswers = m_answerDao.getAnswersByAuthor(m_id);

		double averageQuestionsRating = 0;
		for (Question userQuestion : userQuestions) {
			averageQuestionsRating += userQuestion.getRating() / userQuestions.size();
		}

		double averageAnswersRating = 0;
		for (Answer userAnswer : userAnswers) {
			averageAnswersRating += userAnswer.getRating() / userAnswers.size();
		}

		return 0.2 * averageQuestionsRating + 0.8 * averageAnswersRating;
	}

	/**
	 * Set the user's password to a new one
	 * 
	 * @param password
	 *            the new password
	 * @throws Exception
	 *             if DAO fails
	 */
	public void setPassword(String password) throws Exception {
		m_userDao.setUserPassword(m_id, password);
	}

	/**
	 * Set the user's description to a new one
	 * 
	 * @param description
	 *            the new description
	 * @throws Exception
	 *             if DAO fails
	 */
	public void setDescription(String description) throws Exception {
		m_userDao.setUserDescription(m_id, description);
	}

	/**
	 * Set the user's photo to a new one
	 * 
	 * @param photoUrl
	 *            the new photo's URL
	 * @throws Exception
	 *             if DAO fails
	 */
	public void setPhoto(String photoUrl) throws Exception {
		m_userDao.setUserPhoto(m_id, photoUrl);
	}

	/**
	 * Set the user's phone number to a new one
	 * 
	 * @param phoneNum
	 *            the new phone number
	 * @throws Exception
	 *             if DAO fails
	 */
	public void setPhoneNum(String phoneNum) throws Exception {
		m_userDao.setUserPhoneNum(m_id, phoneNum);
	}

}
