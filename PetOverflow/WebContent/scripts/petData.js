(function (angular) {

	var app = angular.module('petData', []);

	app.factory('PetData', ['$http', '$log', function ($http, $log) {

		function postQuestion(text, topics) {
			return $http({
				method: 'POST',
				url: '/PetOverflow/question',
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
				params: {
					data: JSON.stringify({
						size: size,
						offset: offset	
					})
				}
			});
		}

		function getQuestionsNewest(size, offset) {
			return $http({
				method: 'GET',
				url: '/PetOverflow/question/newest',
				params: {
					data: JSON.stringify({
						size: size,
						offset: offset	
					})
				}
			});
		}

		function getQuestionsExisting(size, offset) {
			return $http({
				method: 'GET',
				url: '/PetOverflow/question/best',
				params: {
					data: JSON.stringify({
						size: size,
						offset: offset	
					})
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
				url: '/PetOverflow/answer/' + id
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
				url: '/PetOverflow/user/' + id
			});
		}

		function getUserAnswers(id, size, offset) {
			return $http({
				method: 'GET',
				url: '/PetOverflow/user/' + id + '/answers',
				params: { 
					data: JSON.stringify({
						size: size,
						offset: offset
					})
				}
			});
		}

		function getUserQuestions(id, size, offset) {
			return $http({
				method: 'GET',
				url: '/PetOverflow/user/' + id + '/questions',
				params: {
					data: JSON.stringify({
						size: size,
						offset: offset
					})
				}
			});
		}

		function getUsersLeaders(size, offset) {
			return $http({
				method: 'GET',
				url: '/PetOverflow/user/leaders',
				params: {
					data: JSON.stringify({
						size: size,
						offset: offset
					})
				}
			});
		}

		function getTopicsPopular(size, offset) {
			return $http({
				method: 'GET',
				url: '/PetOverflow/topic/popular',
				params: {
					data: JSON.stringify({
						size: size,
						offset: offset
					})
				}
			});
		}

		function getTopicQuestions(topic, size, offset) {
			return $http({
				method: 'GET',
				url: '/PetOverflow/topic/' + topic + '/questions',
				params: {
					data: JSON.stringify({
						size: size,
						offset: offset
					})
				}
			});
		}

		function putUserDescription(userInfo) {
			return $http({
				method: 'PUT',
				url: '/PetOverflow/user/me/description',
				data: {
					username: userInfo.username,
					id: userInfo.id,
					description: userInfo.description
				}
			})
		};
		function putUserWantsSms(userInfo) {
			return $http({
				method: 'PUT',
				url: '/PetOverflow/user/me/wantsSms',
				data: {
					username: userInfo.username,
					id: userInfo.id,
					wantsSms: userInfo.wantsSms
				}
			})
		};
		function putUserPhoto(userInfo) {
			return $http({
				method: 'PUT',
				url: '/PetOverflow/user/me/photo',
				data: {
					username: userInfo.username,
					id: userInfo.id,
					photoUrl: userInfo.photoUrl
				}
			})
		};
		function putUserPhone(userInfo) {
			return $http({
				method: 'PUT',
				url: '/PetOverflow/user/me/phone',
				data: {
					username: userInfo.username,
					id: userInfo.id,
					phoneNumber: userInfo.phoneNumber
				}
			})
		};

		function getQuestionsByText(text, size, offset) {
			return $http({
				method: 'GET',
				url: '/PetOverflow/question/search',
				params: {
					data: JSON.stringify({
						size: size,
						offset: offset
					})
				}
			});
		}

		function getTopicsByText(text, size, offset) {
			return $http({
				method: 'GET',
				url: '/PetOverflow/topic/search',
				params: {
					data: JSON.stringify({
						size: size,
						offset: offset
					})
				}
			});
		}


		return {
			// Questions
			postQuestion: postQuestion,
			getQuestion: getQuestion,
			getQuestionAnswers: getQuestionAnswers,
			getQuestionsNewest: getQuestionsNewest,
			getQuestionsExisting: getQuestionsExisting,
			putQuestionVote: putQuestionVote,
			getQuestionsByText: getQuestionsByText,

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
			putUserDescription: putUserDescription,
			putUserWantsSms: putUserWantsSms,
			putUserPhoto: putUserPhoto,
			putUserPhone: putUserPhone,

			// Topics
			getTopicsPopular: getTopicsPopular,
			getTopicQuestions: getTopicQuestions,
			getTopicsByText: getTopicsByText
		};

	}]);


})(angular);