package petoverflow.dto;

/**
 * The AnswerDto holds information about an answer. This object is used to
 * transfer that data easily.
 */
public class AnswerDto {

	public int id;

	public String text;

	public int authorId;

	public double rating;

	// timestamp in milliseconds since Jan 1990
	public long timestamp;

	public int questionId;

	public int voteStatus;

}
