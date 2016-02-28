package petoverflow.dto;

import java.util.ArrayList;
import java.util.List;

import petoverflow.dao.items.Topic;

/**
 * The TopicDto holds information about a question. This object is used to
 * transfer that data easily.
 */
public class TopicDto {

	public TopicDto(Topic topic) throws Exception {
		name = topic.getName();
		rating = topic.getRating();
	}

	public String name;

	public double rating;

	/**
	 * Creates TopicDto objects from Topic objects, given a user ID
	 * 
	 * @param topics
	 *            The topic
	 * @param userId
	 *            The user that needs these topics
	 * @return TopicDto objects to be sent back to the user
	 * @throws Exception
	 */
	public static List<TopicDto> listToDto(List<Topic> topics, int userId) throws Exception {
		List<TopicDto> listDto = new ArrayList<TopicDto>();
		for (Topic topic : topics) {
			listDto.add(new TopicDto(topic));
		}
		return listDto;
	}

}
