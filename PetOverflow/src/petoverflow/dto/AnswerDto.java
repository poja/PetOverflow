package petoverflow.dto;

import java.sql.Timestamp;

/**
 * The AnswerDto holds information about an answer. This object is used to
 * transfer that data easily.
 */
public class AnswerDto {

	public int id;

	public String text;

	public int authorId;

	public double rating;

	public Timestamp timestamp;

	public int questionId;

	public int voteStatus;

}
