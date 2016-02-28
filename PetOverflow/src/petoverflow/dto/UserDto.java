package petoverflow.dto;

import java.util.ArrayList;
import java.util.List;

import petoverflow.dao.items.Topic;
import petoverflow.dao.items.User;

/**
 * The UserDto holds information about a user. This object is used to transfer
 * that data easily.
 */
public class UserDto {

	public UserDto(User user) throws Exception {
		id = user.getId();
		username = user.getUsername();
		nickname = user.getNickname();
		description = user.getDescription();
		photoUrl = user.getPhotoUrl();
		rating = user.getRating();
		phoneNumber = user.getPhoneNum();
		wantsSms = user.getWantsSms();

		List<Topic> bestTopics = user.getBestTopics();
		List<TopicDto> bestTopicsDto = new ArrayList<TopicDto>();
		for (Topic topic : bestTopics) {
			TopicDto topicDto = new TopicDto(topic);
			bestTopicsDto.add(topicDto);
		}
		expertise = bestTopicsDto;
	}

	public int id;

	public String username;

	public String nickname;

	public String description;
	
	public String photoUrl;

	public double rating;

	public String phoneNumber;

	public boolean wantsSms;

	public List<TopicDto> expertise;


	/**
	 * Creates UserDto objects from User objects
	 * 
	 * @param users The users
	 * @return UserDto objects
	 * @throws Exception
	 */
	public static List<UserDto> listToDto(List<User> users) throws Exception {
		List<UserDto> listDto = new ArrayList<UserDto>();
		for (User user : users) {
			listDto.add(new UserDto(user));
		}
		return listDto;
	}

}
