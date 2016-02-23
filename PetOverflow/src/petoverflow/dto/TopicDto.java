package petoverflow.dto;

import java.util.ArrayList;
import java.util.List;

import petoverflow.dao.items.Topic;

public class TopicDto {

	public TopicDto(Topic topic) throws Exception {
		name = topic.getName();
		rating = topic.getRating();
	}

	public String name;

	public double rating;

	public static List<TopicDto> listToDto(List<Topic> topics, int userId) throws Exception {
		List<TopicDto> listDto = new ArrayList<TopicDto>();
		for (Topic topic : topics) {
			listDto.add(new TopicDto(topic));
		}
		return listDto;
	}

}
