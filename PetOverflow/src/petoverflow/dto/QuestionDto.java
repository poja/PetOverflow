package petoverflow.dto;

import java.sql.Timestamp;
import java.util.List;

public class QuestionDto {

	public int id;
	
	public String text;
	
	public int authorId;
	
	public double rating;
	
	public Timestamp timeStamp;
	
	public List<String> topics;
	
}
