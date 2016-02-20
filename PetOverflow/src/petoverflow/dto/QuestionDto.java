package petoverflow.dto;

import java.sql.Timestamp;
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

	public Timestamp timeStamp;

	public List<String> topics;

	public int voteStatus;

}
