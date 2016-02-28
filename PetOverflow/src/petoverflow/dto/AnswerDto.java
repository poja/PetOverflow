package petoverflow.dto;

import java.util.ArrayList;
import java.util.List;

import petoverflow.dao.items.Answer;
import petoverflow.dao.items.Vote;
import petoverflow.dao.items.Vote.VoteType;

/**
 * The AnswerDto holds information about an answer. This object is used to
 * transfer that data easily.
 */
public class AnswerDto {

	public AnswerDto(Answer answer, int userId) throws Exception {
		id = answer.getId();
		text = answer.getText();
		authorId = answer.getAuthor().getId();
		rating = answer.getRating();
		timestamp = answer.getTimestamp().getTime();
		questionId = answer.getQuestion().getId();

		int voteStatusValue = 0;
		List<Vote> votes = answer.getDaoManager().getAnswerVoteDao().getAnswerVotes(id);
		for (Vote vote : votes) {
			if (vote.getVoterId() == userId) {
				voteStatusValue = vote.getType() == VoteType.Up ? 1 : -1;
				break;
			}
		}
		voteStatus = voteStatusValue;
	}

	public int id;

	public String text;

	public int authorId;

	public double rating;

	// timestamp in milliseconds since Jan 1990
	public long timestamp;

	public int questionId;

	public int voteStatus;

	/**
	 * Creates AnswerDto objects from Answer objects, given a user ID
	 * 
	 * @param answers
	 *            The answers
	 * @param userId
	 *            The user of these answers
	 * @return AnswerDto objects to be sent back to the user
	 * @throws Exception
	 */
	public static List<AnswerDto> listToDto(List<Answer> answers, int userId) throws Exception {
		List<AnswerDto> listDto = new ArrayList<AnswerDto>();
		for (Answer answer : answers) {
			listDto.add(new AnswerDto(answer, userId));
		}
		return listDto;
	}

}
