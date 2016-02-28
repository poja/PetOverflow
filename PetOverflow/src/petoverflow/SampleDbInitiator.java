package petoverflow;

import java.util.ArrayList;
import java.util.List;

import petoverflow.dao.AnswerDao;
import petoverflow.dao.AnswerVoteDao;
import petoverflow.dao.DaoManager;
import petoverflow.dao.QuestionDao;
import petoverflow.dao.QuestionVoteDao;
import petoverflow.dao.UserDao;
import petoverflow.dao.derby.DaoManagerDerby;
import petoverflow.dao.items.Answer;
import petoverflow.dao.items.Question;
import petoverflow.dao.items.User;
import petoverflow.dao.items.Vote;
import petoverflow.dao.items.Vote.VoteType;

/**
 * An optional initiator for the database.
 * 
 * Inserts example information - a few users, questions, answers and votes. The
 * feature can be turned on and off using {@link petoverflow.Config}
 */
public class SampleDbInitiator {

	private static UserDao m_userDao;
	private static QuestionDao m_questionDao;
	private static QuestionVoteDao m_questionVoteDao;
	private static AnswerDao m_answerDao;
	private static AnswerVoteDao m_answerVoteDao;

	private static User barak, yishai, haggai, patrick, vet;

	/**
	 * Initiate sample users
	 * 
	 * @throws Exception
	 */
	private static void initiateUsers() throws Exception {
		barak = m_userDao.createUser("barak", "12304", "barak", "I am Barak!", "http://tinyurl.com/pet-lightning",
				"+972527567813", false);
		yishai = m_userDao.createUser("yishai", "43201", "yishai", "I am Yishai!", "http://tinyurl.com/h33ctz3",
				"+972527567813", false);
		haggai = m_userDao.createUser("haggaiRoi5", "50678", "haggaiTeacher", "I am Haggai, the teacher!",
				"http://tinyurl.com/pet-professor", "+972527567813", false);
		patrick = m_userDao.createUser("patrick", "66666", "patrickStar", "Umm....",
				"http://www.unitedspongebob.com/patrickbio.jpg", "+972527567813", false);
		vet = m_userDao.createUser("vet", "02801", "theVeterinarian", "I make pets healthy.",
				"http://tinyurl.com/oougk57", "+972527567813", false);
	}

	private static void initiateQuestion1() throws Exception {
		List<String> topics = new ArrayList<String>();
		topics.add("walks");
		topics.add("statistics");
		topics.add("habits");
		Question q1 = m_questionDao.createQuestion(
				"Hello, I am a veterinarian, and I would like to know something about the habits of you pet owners. "
						+ "How many times a week do you usually take your pet out for a walk? "
						+ "Please mention what kind of pet you have.",
				vet.getId(), topics);
		m_questionVoteDao.addVote(q1.getId(), new Vote(yishai.getId(), VoteType.Up));
		m_questionVoteDao.addVote(q1.getId(), new Vote(haggai.getId(), VoteType.Up));
		m_questionVoteDao.addVote(q1.getId(), new Vote(patrick.getId(), VoteType.Up));
		Answer a1 = m_answerDao.createAnswer(
				"Well, I take out my pet rock every single day. Twice a day. My rock couldn't be happier.",
				patrick.getId(), q1.getId());
		Answer a2 = m_answerDao.createAnswer("Turtles don't need to be out, they just love the cozy house!",
				yishai.getId(), q1.getId());
		Answer a3 = m_answerDao.createAnswer(
				"I have a few cats and they just live outside so I don't " + "really take them out for walks.",
				barak.getId(), q1.getId());
		m_answerVoteDao.addVote(a1.getId(), new Vote(barak.getId(), VoteType.Up));
		m_answerVoteDao.addVote(a2.getId(), new Vote(haggai.getId(), VoteType.Up));
		m_answerVoteDao.addVote(a3.getId(), new Vote(yishai.getId(), VoteType.Up));
	}

	private static void initiateQuestion2() throws Exception {
		List<String> topics = new ArrayList<String>();
		topics.add("food");
		topics.add("fish");
		topics.add("goldfish");
		Question q2 = m_questionDao.createQuestion(
				"I have a pet goldfish. A few days ago, it started being very upset."
						+ " I think the problem is that I am not feeding it. What is your opinion?",
				patrick.getId(), topics);
		m_questionVoteDao.addVote(q2.getId(), new Vote(yishai.getId(), VoteType.Down));
		Answer a1 = m_answerDao.createAnswer("Well of course you should feed the fish! The recommended"
				+ " amount of pet food is one teaspoon per day. Make sure the fish eats"
				+ " at least a bit every day if you do not want to it to starve!", vet.getId(), q2.getId());
		m_answerVoteDao.addVote(a1.getId(), new Vote(barak.getId(), VoteType.Up));
		m_answerVoteDao.addVote(a1.getId(), new Vote(patrick.getId(), VoteType.Up));
		m_answerVoteDao.addVote(a1.getId(), new Vote(yishai.getId(), VoteType.Up));
		m_answerVoteDao.addVote(a1.getId(), new Vote(haggai.getId(), VoteType.Up));
	}

	private static void initiateQuestion3() throws Exception {
		List<String> topics = new ArrayList<String>();
		topics.add("cats");
		topics.add("sickness");
		Question q4 = m_questionDao.createQuestion("Our family has a few cats. About a month ago, I started noticing"
				+ " that one of the female cats has become much more noisy. Also, her stomach became large!"
				+ " What hapenned to our cat??", barak.getId(), topics);
		m_questionVoteDao.addVote(q4.getId(), new Vote(vet.getId(), VoteType.Up));
		Answer a1 = m_answerDao.createAnswer(
				"The same thing happened to me about a year ago. "
						+ "It turned out that these are signs of pregnancy!! I now have cute little kitties!",
				haggai.getId(), q4.getId());
		Answer a2 = m_answerDao.createAnswer("These are known signs of cat pregnancy. You should try to also"
				+ " spot other strange behaviors, and visit a local vet.", vet.getId(), q4.getId());
		m_answerVoteDao.addVote(a1.getId(), new Vote(barak.getId(), VoteType.Up));
		m_answerVoteDao.addVote(a1.getId(), new Vote(vet.getId(), VoteType.Up));
		m_answerVoteDao.addVote(a2.getId(), new Vote(barak.getId(), VoteType.Up));
	}

	private static void initiateUnanswered() throws Exception {
		List<String> topics = new ArrayList<String>();
		topics.add("turtles");
		topics.add("names");
		m_questionDao.createQuestion("Today I bought a pet turtle. I don't know what I should name it."
				+ " Can you help me find a good name?", yishai.getId(), topics);

		topics.clear();
		topics.add("trips");
		topics.add("walks");
		topics.add("dogs");
		topics.add("sun");
		topics.add("sunny");
		topics.add("shephard");
		topics.add("haifa");
		Question q1 = m_questionDao.createQuestion(
				"My German Shephard and I love to go on small trips. The problem is, we don't know many nice places in Haifa, "
						+ "because we are new here. We like sunny places, and we have already been at the beach. "
						+ "Do you have any recommandations, or a reference to a site I can search in?",
				vet.getId(), topics);
		m_questionVoteDao.addVote(q1.getId(), new Vote(haggai.getId(), VoteType.Up));

		topics.clear();
		topics.add("birds");
		topics.add("parrots");
		topics.add("AfricanGrey");
		topics.add("food");
		topics.add("timing");
		m_questionDao.createQuestion("I have two African Grey parrots (Jaco). I know I should feed"
				+ " them twice a day. What are the best times and why?", barak.getId(), topics);

	}

	public static void run() {
		try {
			DaoManager m_daoManager = DaoManagerDerby.getInstance();
			m_userDao = m_daoManager.getUserDao();
			m_questionDao = m_daoManager.getQuestionDao();
			m_answerDao = m_daoManager.getAnswerDao();
			m_questionVoteDao = m_daoManager.getQuestionVoteDao();
			m_answerVoteDao = m_daoManager.getAnswerVoteDao();

			initiateUsers();
			initiateQuestion1();
			initiateQuestion2();
			initiateQuestion3();
			initiateUnanswered();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
