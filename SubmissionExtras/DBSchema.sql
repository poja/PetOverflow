/* PetOverflow 
 * 
 * The following commands were used to define and manipulate
 * the PetOverflow Database.
 */

-- Connection initiation
 connect 'jdbc:derby:PetOvDb;create=true';


 /* User Table Creation
  * 
  * Notice the 'phoneNum' and 'wantsSms' columns, that are used 
  * for the extended notification service.
  */
 CREATE TABLE PetOwner (

 	ID INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
 	username VARCHAR(10) NOT NULL UNIQUE,
 	password VARCHAR(8) NOT NULL,
 	nickname VARCHAR(20) NOT NULL,
 	description VARCHAR(50) NOT NULL,
 	photoUrl LONG VARCHAR,
 	phoneNum VARCHAR(20),
 	wantsSms BOOLEAN NOT NULL

 );

/* Question Table Creation 
 * 
 * Each question has a foreign key of its asker
 */
CREATE TABLE Question (

	ID INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
	text VARCHAR(300) NOT NULL,
	authorId INTEGER NOT NULL,
	timestamp TIMESTAMP NOT NULL,

	FOREIGN KEY (authorId) REFERENCES PetOwner(id)

);


/* Answer Table Creation
 * 
 * Each answer has two foreign keys - the asker and the question
 */
CREATE TABLE Answer (

	ID INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
	text  VARCHAR(300) NOT NULL,
	authorId INTEGER NOT NULL,
	questionId INTEGER NOT NULL,
	timestamp TIMESTAMP NOT NULL,

	FOREIGN KEY (authorId) REFERENCES PetOwner(id),
	FOREIGN KEY (questionId) REFERENCES Question(id)

);

 /* QuestionVote Table Creation
  * 
  * The primary key is the pair (voterId, questionId),
  * and each one is also a foreign key. (Many-to-many relationship)
  */
CREATE TABLE QuestionVote (

	voterId INTEGER NOT NULL,
	questionId INTEGER NOT NULL,
	voteType BOOLEAN NOT NULL, 		-- true='+',false='-'

	PRIMARY KEY (voterId, questionId),
	FOREIGN KEY (voterId) REFERENCES PetOwner(id),
	FOREIGN KEY (questionId) REFERENCES Question(id)

);

 /* AnswerVote Table Creation
  * 
  * The primary key is the pair (voterId, answerId),
  * and each one is also a foreign key. (Many-to-many relationship)
  */
CREATE TABLE AnswerVote (

	voterId INTEGER NOT NULL,
	answerId INTEGER NOT NULL,
	voteType BOOLEAN NOT NULL, 		-- true='+',false='-'

	PRIMARY KEY (voterId, answerId),
	FOREIGN KEY (voterId) REFERENCES PetOwner(id),
	FOREIGN KEY (answerId) REFERENCES Answer(id)

);

/* QuestionTopic Table Creation
 * 
 * Describes a many-to-many relationship between questions and topics
 */
CREATE TABLE QuestionTopic (

	questionId INTEGER NOT NULL,
	topic VARCHAR(50) NOT NULL,

	FOREIGN KEY (questionId) REFERENCES Question(id)

);


/* Inserting values into the User Database */
INSERT INTO PetOwner 
	(username, password, nickname, description, photoUrl, phoneNum, wantsSms)
VALUES
	('haggaiRoi5', '50678', 'haggaiTeacher', 'I am Haggai, the teacher!', 
		'http://tinyurl.com/hd626y8', '+972527567813', false);

INSERT INTO PetOwner 
	(username, password, nickname, description, photoUrl, phoneNum, wantsSms)
VALUES
	('vet', '02801', 'theVeterinarian', 'I make pets healthy.', 
		'http://tinyurl.com/oougk57', '+972527567813', false);

/* Inserting values into the Question Database */
INSERT INTO Question
	(text, authorId, timestamp)
VALUES
	('How many times a week do you usually take your pet out for a walk?',
		1, CURRENT_TIMESTAMP);

/* Inserting into the Answer Database */
INSERT INTO Answer
	(text, authorId, questionId, timestamp)
VALUES
	('Every day.', 1, 1, CURRENT_TIMESTAMP);

/* Inserting into the QuestionVote Database */
INSERT INTO QuestionVote
	(voterId, questionId, voteType)
VALUES
	(2, 1, true);

/* Inserting into the AnswerVote Database */
INSERT INTO AnswerVote
	(voterId, answerId, voteType)
VALUES
	(2, 1, true);

/* Add a topic to a question */
INSERT INTO QuestionTopic
	(questionId, topic)
VALUES
	(1, 'walks');



/* Updating User Table, one attribute at a time */

UPDATE PetOwner 
SET description = 'I make pets VERY healthy.'
WHERE ID = 2;

UPDATE PetOwner
SET password = '682912'
WHERE ID = 2;

UPDATE PetOwner
SET phoneNum = '+972526666666'
WHERE ID = 2;

UPDATE PetOwner
SET wantsSms = true
WHERE ID = 2;

UPDATE PetOwner
SET photoUrl = 'http://tinyurl.com/oougk57'
WHERE ID = 2;


/* Selecting information from the table */

/* Finding information about a certain user by ID */
SELECT * FROM PetOwner
WHERE ID = 2;
/* Finding information about a certain user by username */
SELECT * FROM PetOwner
WHERE username = 'haggaiRoi5';
/* Finding a specific attribute by ID */
SELECT username FROM PetOwner
WHERE ID = 2;

SELECT nickname FROM PetOwner
WHERE ID = 2;

SELECT description FROM PetOwner
WHERE ID = 2;

SELECT photoUrl FROM PetOwner
WHERE ID = 2;

SELECT wantsSms FROM PetOwner
WHERE ID = 2;
/* Finding ID and password of a user, by username (for authentication) */
SELECT id, password FROM PetOwner
WHERE username = 'vet';



/* Finding a question with a specific ID */
SELECT * FROM Question
WHERE ID = 1;
/* Finding all questions by a certain author */
SELECT * FROM Question
WHERE authorId = 1;
/* Get Specific info about a question */
SELECT text FROM Question
WHERE ID = 1;

SELECT authorId FROM Question
WHERE ID = 1;

SELECT timestamp FROM Question
WHERE ID = 1;


/* Get all votes to a certain question */
SELECT * FROM QuestionVote
WHERE questionId = 1;

/* Get questions of a certain topic */
SELECT questionId FROM QuestionTopic
WHERE topic = 'walks';
/* Get topics of a certain question */
SELECT topic FROM QuestionTopic
WHERE questionId = 1;



/* Selecting a specific attribute of an answer, by ID */
SELECT text FROM Answer
WHERE ID = 1;

SELECT authorId FROM Answer
WHERE ID = 1;

SELECT questionId FROM Answer
WHERE ID = 1;

SELECT timestamp FROM Answer
WHERE ID = 1;

/* Finding answers answers of a certain author or question */
SELECT ID FROM Answer
WHERE authorId = 2;

SELECT ID FROM Answer
WHERE questionId = 1;


/* Finding answer votes by answer */
SELECT * from AnswerVote
WHERE answerId = 1;
