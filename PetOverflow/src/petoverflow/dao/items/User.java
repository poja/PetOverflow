package petoverflow.dao.items;

import java.util.ArrayList;
import java.util.List;

import petoverflow.dao.AnswerDao;
import petoverflow.dao.DaoManager;
import petoverflow.dao.DaoObject;
import petoverflow.dao.QuestionDao;
import petoverflow.dao.UserDao;
import petoverflow.dao.utility.Rated;
import petoverflow.dto.TopicDto;
import petoverflow.dto.UserDto;

/**
 * The User class represent a user in the PetOverflow system. This class uses
 * DAO to get it's data and in most of it's methods exceptions are thrown if the
 * DAO fails.
 * 
 * @see UserDao
 * @see QuestionDao
 * @see AnswerDao
 */
public class User extends DaoObject implements Rated {

	/**
	 * Id of this user
	 */
	private final int m_id;

	private static final int BEST_TOPICS_SIZE = 5;

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
	public User(DaoManager daoManager, int id) {
		super(daoManager);
		m_id = id;
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
		return m_daoManager.getUserDao().getUserUsername(m_id);
	}

	/**
	 * Get the nickname of this user
	 * 
	 * @return this user's nickname
	 * @throws Exception
	 *             if DAO fails
	 */
	public String getNickname() throws Exception {
		return m_daoManager.getUserDao().getUserNickname(m_id);
	}

	/**
	 * Get the description of this user
	 * 
	 * @return this user' description
	 * @throws Exception
	 *             if DAO fails
	 */
	public String getDescription() throws Exception {
		return m_daoManager.getUserDao().getUserDescription(m_id);
	}

	/**
	 * Get the URL of the photo of this user
	 * 
	 * @return this user's photo URL
	 * @throws Exception
	 *             if DAO fails
	 */
	public String getPhotoUrl() throws Exception {
		return m_daoManager.getUserDao().getUserPhotoURL(m_id);
	}

	/**
	 * Get the phone number of this user
	 * 
	 * @return this user's phone number
	 * @throws Exception
	 *             if DAO fails
	 */
	public String getPhoneNum() throws Exception {
		return m_daoManager.getUserDao().getUserPhoneNum(m_id);
	}

	/**
	 * Check if the user wants SMS notifications
	 * 
	 * @return true if the user wants notifications, else false
	 * @throws Exception
	 */
	public boolean getWantsSms() throws Exception {
		return m_daoManager.getUserDao().getUserWantsSms(m_id);
	}

	/**
	 * Get the rating of this user
	 * 
	 * @return this user's rating
	 * @throws Exception
	 *             if DAO fails
	 */
	public double getRating() throws Exception {
		List<Question> userQuestions = m_daoManager.getQuestionDao().getQuestionsByAuthorAll(m_id);
		List<Answer> userAnswers = m_daoManager.getAnswerDao().getAnswersByAuthorAll(m_id);

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

	public List<Topic> getBestTopics() throws Exception {
		return m_daoManager.getUserDao().getUserBestTopics(m_id, BEST_TOPICS_SIZE);
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
		m_daoManager.getUserDao().setUserPassword(m_id, password);
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
		m_daoManager.getUserDao().setUserDescription(m_id, description);
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
		m_daoManager.getUserDao().setUserPhoto(m_id, photoUrl);
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
		m_daoManager.getUserDao().setUserPhoneNum(m_id, phoneNum);
	}

	/**
	 * Create a user DTO that holds the public information about the user
	 * 
	 * @return user DTO object with the user information
	 * @throws Exception
	 *             if DAO fail
	 */
	public UserDto toUserDto() throws Exception {
		UserDto user = new UserDto();
		user.id = m_id;
		user.username = getUsername();
		user.nickname = getNickname();
		user.description = getDescription();
		user.photoUrl = getPhotoUrl();
		user.rating = getRating();
		user.phoneNumber = getPhoneNum();
		user.wantsSms = getWantsSms();

		List<Topic> bestTopics = getBestTopics();
		List<TopicDto> bestTopicsDto = new ArrayList<TopicDto>();
		for (Topic topic : bestTopics) {
			TopicDto topicDto = topic.toTopicDto();
			bestTopicsDto.add(topicDto);
		}
		user.expertise = bestTopicsDto;

		return user;
	}

	@Override
	public int hashCode() {
		return m_id;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		} else if (o == this) {
			return true;
		} else if (!(o instanceof User)) {
			return false;
		}

		User other = (User) o;
		return m_id == other.m_id;
	}

}
