(function (angular) {

	var app = angular.module('petData', []);

	app.factory('PetData', ['$http', '$log', function ($http, $log) {

		/**
		 *	Creates a fake Promise, that always works, 
		 *	and calls the "success" callback with the given data
		 */
		function mockPromise(data) {
			return {
				then: function (success, failure) {
					success({ 
						'data': data 
					});
				}
			};
		}

		function postQuestion(text, topics) {
			return $http({
				method: 'POST',
				url: '/PetOverflow/question'
				data: {
					text: text,
					topics: topics
				}
			});
		}

		function getQuestion(id) {
			return $http({
				method: 'GET',
				url: '/PetOverflow/question/' + id
			});
		}

		function getQuestionAnswers(id, size, offset) {
			return $http({
				method: 'GET',
				url: '/PetOverflow/question/' + id + '/answers',
				data: {
					size: size,
					offset, offset
				}
			});
		}

		function getQuestionsNewest(size, offset) {
			return $http({
				method: 'GET',
				url: '/PetOverflow/question/newest'
				data: {
					size: size,
					offset: offset
				}
			});
		}

		function putQuestionVote(id, voteType) {
			return $http({
				method: 'PUT',
				url: '/PetOverflow/question/' + id + '/vote',
				data: {
					voteType: voteType
				}
			});
		}

		function postAnswer(text, questionId) {
			return $http({
				method: 'POST',
				url: '/PetOverflow/answer',
				data: {
					text: text,
					questionId: questionId
				}
			});
		}

		function getAnswer(id) {
			return $http({
				method: 'GET',
				url: '/PetOverflow/answer/' + id;
			});
		}

		function putAnswerVote(id, voteType) {
			return $http({
				method: 'PUT',
				url: '/PetOverflow/answer/' + id + "/vote",
				data: {
					voteType: voteType
				}
			});
		}

		function postUser(userInfo) {
			return $http({
				method: 'POST',
				url: '/PetOverflow/user',
				data: userInfo
			});
		}

		function getUser(id) {
			return $http({
				method: 'GET',
				url: '/PetOverflow/user/' + id;
			});
		}

		function getUserAnswers(id, size, offset) {
			return $http({
				method: 'GET',
				url: '/PetOverflow/user/' + id + '/answers',
				data: {
					size: size,
					offset: offset
				}
			});
		}

		function getUserQuestions(id, size, offset) {
			return $http({
				method: 'GET',
				url: '/PetOverflow/user/' + id + 'questions',
				data: {
					size: size,
					offset: offset
				}
			});
		}

		function getUsersLeaders(size, offset) {
			return $http({
				method: 'GET',
				url: '/PetOverflow/user/leaders',
				data: {
					size: size,
					offset: offset
				}
			});
		}

		function getTopicsPopular(size, offset) {
			return $http({
				method: 'GET',
				url: '/PetOverflow/topic/popular'
			});
		}

		function getTopicQuestions(topic, size, offset) {
			return $http({
				method: 'GET',
				url: '/PetOverflow/topic/' + topic + '/questions',
				data: {
					size: size,
					offset: offset
				}
			});
		}

		return {
			// Questions
			postQuestion: postQuestion,
			getQuestion: getQuestion,
			getQuestionAnswers: getQuestionAnswers,
			getQuestionsNewest: getQuestionsNewest,
			putQuestionVote: putQuestionVote,

			// Answers
			postAnswer: postAnswer,
			getAnswer: getAnswer,
			putAnswerVote: putAnswerVote,

			// Users
			postUser: postUser,
			getUser: getUser,
			getUserAnswers: getUserAnswers,
			getUserQuestions: getUserQuestions,
			getUsersLeaders: getUsersLeaders,

			// Topics
			getTopicsPopular: getTopicsPopular,
			getTopicQuestions: getTopicQuestions,

			getExistingQuestions: getExistingQuestions,
			getTopicById: getTopicById
		};

	}]);


})(angular);