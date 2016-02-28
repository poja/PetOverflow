package petoverflow.dto;

import java.util.ArrayList;
import java.util.List;

import petoverflow.dao.items.Question;
import petoverflow.dao.items.Topic;
import petoverflow.dao.items.Vote;
import petoverflow.dao.items.Vote.VoteType;

/**
 * The QuestionDto holds information about a question. This object is used to
 * transfer that data easily.
 */
public class QuestionDto {

	public QuestionDto(Question question, int userId) throws Exception {
		id = question.getId();
		text = question.getText();
		authorId = question.getAuthor().getId();
		rating = question.getRating();
		voteCount = question.getVoteCount();
		timestamp = question.getTimestamp().getTime();
		bestAnswerId = question.getBestAnswer();

		List<Topic> topicsValue = question.getTopics();
		topics = new ArrayList<String>();
		for (Topic topic : topicsValue) {
			topics.add(topic.getName());
		}

		int voteStatusValue = 0;
		List<Vote> votes = question.getDaoManager().getQuestionVoteDao().getQuestionVotes(id);
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

	public double voteCount;

	public long timestamp;

	public List<String> topics;

	public int voteStatus;

	public Integer bestAnswerId;

	/**
	 * Creates QuestionDto objects from Question objects, given a user ID
	 * 
	 * @param question
	 *            The questions
	 * @param userId
	 *            The user of these questions
	 * @return QuestionDto objects to be sent back to the user
	 * @throws Exception
	 */
	public static List<QuestionDto> listToDto(List<Question> questions, int userId) throws Exception {
		List<QuestionDto> listDto = new ArrayList<QuestionDto>();
		for (Question question : questions) {
			listDto.add(new QuestionDto(question, userId));
		}
		return listDto;
	}

}
