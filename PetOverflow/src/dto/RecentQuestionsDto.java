package dto;

public class RecentQuestionsDto {

	private String question;
	
	public RecentQuestionsDto(String q) {
		question = q;
	}
	
	public String getQuestion() {
		return question;
	}
}
