package petoverflow.dao.derby;

/**
 * All the naming configurations of the Derby database.
 */
public class DerbyConfig {

	public static final String ID = "ID";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String NICKNAME = "nickname";
	public static final String DESCRIPTION = "description";
	public static final String PHOTO_URL = "photoUrl";
	public static final String PHONE_NUM = "phoneNum";
	public static final String WANTS_SMS = "wantsSms";
	public static final String TEXT = "text";
	public static final String AUTHOR_ID = "authorId";
	public static final String TIMESTAMP = "timestamp";
	public static final String QUESTION_ID = "questionId";
	public static final String ANSWER_ID = "answerId";
	public static final String VOTER_ID = "voterId";
	public static final String VOTE_TYPE = "voteType";
	public static final String TOPIC = "topic";

	private static final String ID_TYPE = "INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY";
	private static final String USERNAME_TYPE = "VARCHAR(10) NOT NULL UNIQUE";
	private static final String PASSWORD_TYPE = "VARCHAR(8) NOT NULL";
	private static final String NICKNAME_TYPE = "VARCHAR(20) NOT NULL";
	private static final String DESCRIPTION_TYPE = "VARCHAR(50) NOT NULL";
	private static final String PHOTO_URL_TYPE = "LONG VARCHAR";
	private static final String PHONE_NUM_TYPE = "VARCHAR(20)";
	private static final String WANTS_SMS_TYPE = "BOOLEAN NOT NULL";
	private static final String TEXT_TYPE = "VARCHAR(300) NOT NULL";
	private static final String AUTHOR_ID_TYPE = "INTEGER NOT NULL";
	private static final String TIMESTAMP_TYPE = "TIMESTAMP NOT NULL";
	private static final String QUESTION_ID_TYPE = "INTEGER NOT NULL";
	private static final String ANSWER_ID_TYPE = "INTEGER NOT NULL";
	private static final String VOTER_ID_TYPE = "INTEGER NOT NULL";
	private static final String VOTE_TYPE_TYPE = "BOOLEAN NOT NULL"; // true='+',false='-'
	private static final String TOPIC_TYPE = "VARCHAR(50) NOT NULL";

	public static final String DB_NAME = "PetOvDb";

	public static final String USER_TABLE_NAME = "PetOwner";
	public static final String USER_TABLE_CREATE = "CREATE TABLE " + USER_TABLE_NAME + " (" + ID + " " + ID_TYPE + ", "
			+ USERNAME + " " + USERNAME_TYPE + ", " + PASSWORD + " " + PASSWORD_TYPE + ", " + NICKNAME + " "
			+ NICKNAME_TYPE + ", " + DESCRIPTION + " " + DESCRIPTION_TYPE + ", " + PHOTO_URL + " " + PHOTO_URL_TYPE
			+ ", " + PHONE_NUM + " " + PHONE_NUM_TYPE + ", " + WANTS_SMS + " " + WANTS_SMS_TYPE + ")";

	public static final String QUESTION_TABLE_NAME = "Question";
	public static final String QUESTION_TABLE_CREATE = "CREATE TABLE " + QUESTION_TABLE_NAME + " (" + ID + " " + ID_TYPE
			+ ", " + TEXT + " " + TEXT_TYPE + ", " + AUTHOR_ID + " " + AUTHOR_ID_TYPE + ", " + TIMESTAMP + " "
			+ TIMESTAMP_TYPE + ", FOREIGN KEY(" + AUTHOR_ID + ") REFERENCES " + USER_TABLE_NAME + "(" + ID + "))";

	public static final String ANSWER_TABLE_NAME = "Answer";
	public static final String ANSWER_TABLE_CREATE = "CREATE TABLE " + ANSWER_TABLE_NAME + " (" + ID + " " + ID_TYPE
			+ ", " + TEXT + " " + TEXT_TYPE + ", " + AUTHOR_ID + " " + AUTHOR_ID_TYPE + ", " + QUESTION_ID + " "
			+ QUESTION_ID_TYPE + ", " + TIMESTAMP + " " + TIMESTAMP_TYPE + ", FOREIGN KEY (" + AUTHOR_ID
			+ ") REFERENCES " + USER_TABLE_NAME + "(" + ID + "), FOREIGN KEY (" + QUESTION_ID + ") REFERENCES "
			+ QUESTION_TABLE_NAME + "(" + ID + "))";

	public static final String QUESTION_VOTE_TABLE_NAME = "QuestionVote";
	public static final String QUESTION_VOTE_TABLE_CREATE = "CREATE TABLE " + QUESTION_VOTE_TABLE_NAME + " (" + VOTER_ID
			+ " " + VOTER_ID_TYPE + ", " + QUESTION_ID + " " + QUESTION_ID_TYPE + ", " + VOTE_TYPE + " "
			+ VOTE_TYPE_TYPE + ", PRIMARY KEY (" + VOTER_ID + ", " + QUESTION_ID + "), FOREIGN KEY (" + VOTER_ID
			+ ") REFERENCES " + USER_TABLE_NAME + "(" + ID + "), FOREIGN KEY (" + QUESTION_ID + ") REFERENCES "
			+ QUESTION_TABLE_NAME + "(" + ID + "))";

	public static final String ANSWER_VOTE_TABLE_NAME = "AnswerVote";
	public static final String ANSWER_VOTE_TABLE_CREATE = "CREATE TABLE " + ANSWER_VOTE_TABLE_NAME + " (" + VOTER_ID
			+ " " + VOTER_ID_TYPE + ", " + ANSWER_ID + " " + ANSWER_ID_TYPE + ", " + VOTE_TYPE + " " + VOTE_TYPE_TYPE
			+ ", PRIMARY KEY (" + VOTER_ID + ", " + ANSWER_ID + "), FOREIGN KEY (" + VOTER_ID + ") REFERENCES "
			+ USER_TABLE_NAME + "(" + ID + "), FOREIGN KEY (" + ANSWER_ID + ") REFERENCES " + ANSWER_TABLE_NAME + "("
			+ ID + "))";

	public static final String TOPIC_TABLE_NAME = "QuestionTopic";
	public static final String TOPIC_TABLE_CREATE = "CREATE TABLE " + TOPIC_TABLE_NAME + " (" + QUESTION_ID + " "
			+ QUESTION_ID_TYPE + ", " + TOPIC + " " + TOPIC_TYPE + ", FOREIGN KEY (" + QUESTION_ID + ") REFERENCES "
			+ QUESTION_TABLE_NAME + "(" + ID + "))";

}
