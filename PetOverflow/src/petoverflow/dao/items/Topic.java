package petoverflow.dao.items;

import petoverflow.dao.DaoManager;
import petoverflow.dao.DaoObject;
import petoverflow.dao.utility.Rated;
import petoverflow.dto.TopicDto;

public class Topic extends DaoObject implements Rated {

	private String m_name;

	public Topic(DaoManager daoManager, String name) {
		super(daoManager);
		m_name = name;
	}

	public String getName() {
		return m_name;
	}

	@Override
	public double getRating() throws Exception {
		return m_daoManager.getTopicDao().getTopicRating(m_name);
	}

	public TopicDto toTopicDto() throws Exception {
		TopicDto topic = new TopicDto();
		topic.name = m_name;
		topic.rating = getRating();
		return topic;
	}

	public int hashCode() {
		return m_name.hashCode();
	}

	public boolean equals(Object o) {
		if (o == null) {
			return false;
		} else if (o == this) {
			return true;
		} else if (!(o instanceof Topic)) {
			return false;
		}

		Topic other = (Topic) o;
		return m_name.equals(other.m_name);
	}

}
