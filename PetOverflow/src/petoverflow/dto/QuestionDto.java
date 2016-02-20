package petoverflow.dto;

import java.util.List;

/**
 * The QuestionDto holds information about a question. This object is used to
 * transfer that data easily.
 */
public class QuestionDto {

	public int id;

	public String text;

	public int authorId;

	public double rating;

	public double voteCount;

	public long timestamp;

	public List<String> topics;

	public int voteStatus;

	public Integer bestAnswerId;

}
