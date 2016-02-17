(function (angular) {

	var app = angular.module('petData', []);

	app.factory('PetData', function () {

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

		function getNewestQuestions() {
			return mockPromise([{
				id: 62, 	}, {
				id: 23
			}]);
		}

		function getExistingQuestions() {
			return mockPromise([{
				id: 62, 	}, {
				id: 23
			}]);
		}

		function postNewUser(userInfo) {
			return mockPromise({});
		}

		function getUserById(id) {
			return mockPromise({

				id: id,
				username: 'mickey204',
				nickname: 'Mike',
				description: 'Lorem ipsum dolor sit amet, consectetur adipisicing elit. Corporis dolore dignissimos, non iste, ea assumenda perferendis dolor incidunt dolorum eos, autem ut beatae illo quibusdam numquam fuga sed in! Dolores!',
				rating: 4.6,
				photoUrl: 'http://tinyurl.com/rapmonkey',
				expertise: [{
					name: 'bathtubs', id: '321' },{
					name: 'trumpets', id: '520' },{
					name: 'food', id: '194' },{
					name: 'petDolphins', id: '2341' },{
					name: 'haircuts', id: '2045'
				}]

			});
		}

		function getTopicById(id) {
			return mockPromise({

				name: 'lions',
				questions: [{
					id: 5 	}, {
					id: 8
				}]

			});
		}

		function getLeaders() {
			return mockPromise([{
			
				nickname: 'Hamartze',
				id: 494,
				rating: 300,
				photoUrl: 'http://csw.haifa.ac.il/images/Yuri1.jpg',
				expertise: [{
					name: 'algorithms', id: 245 },{
					name: 'probability', id: 927 },{
					name: 'makingStudentsFail', id: 944 },{
					name: 'laughter', id: 944 },{
					name: 'math', id: 944
				}]
			}, {
				nickname: 'DannyDannyMeloohlah',
				id: 999,
				rating: 270,
				photoUrl: 'http://www.cs.tau.ac.il/workshop/dbday/pics/danny.keren%20.jpg',
				expertise: [{
					name: 'imageProcessing', id: 571 }, {
					name: 'computerVision', id: 571 }, {
					name: 'calculus', id: 571 }, {
					name: 'machineLearning', id: 571
				}]
			}, {
				nickname: 'CoolMonkey',
				id: 922,
				rating: 245,
				photoUrl: 'http://tinyurl.com/rapmonkey',
				expertise: [{
					name: 'banana', id: 245 },{
					name: 'banana', id: 927 },{
					name: 'banana', id: 944 },{
					name: 'banana', id: 944 },{
					name: 'banana', id: 944
				}]
			
			}]);
		}

		function getQuestionById(id) {
			return mockPromise({

				id: id,
				text: 'What is a good name for a pet turtle? Today I recieved a pet turtle and it is so amazing. I love it such much and it is cute. The only thing I do not know is what to name it. Help!',
				topics: [{
					name: 'turtles',
					id: 681
				}],
				timestamp: 1288323623006,
				voteCount: 20,
				askerId: 872,
				hasVote: 1

			});
		}

		function getHotTopics() {
			return mockPromise([{

				text: 'bathtubs', size: 45.7, url: '#/topics/231' }, {
				text: 'trumpets',  size: 34, url: '#/topics/231' }, {
				text: 'food', size: 32, url: '#/topics/231' }, {
				text: 'petDolphins', size: 27, url: '#/topics/231' } , {
				text: 'haircuts', size: 20, url: '#/topics/231' }, {
				text: 'bathtubs', size: 45.7, url: '#/topics/231' }, {
				text: 'trumpets',  size: 34, url: '#/topics/231' }, {
				text: 'food', size: 32, url: '#/topics/231' }, {
				text: 'petDolphins', size: 27, url: '#/topics/231' } , {
				text: 'haircuts', size: 20, url: '#/topics/231' 

			}]);
		}

		function getAnswersByQuestionId(id) {
			return mockPromise([{
				id: 294
			}, {
				id: 555
			}]);
		}

		function getAnswerById(id) {
			if (id == 294)
				return mockPromise({
					id: 294,
					text: 'I think that you are stupid that you bought a turtle. Who buys a turtle? It\'s even illegal in some places to sell turtles, because they belong in NATURE. Name it "poor little guy" and give it back.',
					timestamp: 1288323623006,
					voteCount: -4,
					hasVote: -1,
					answererId: 849,
					questionId: 692
				});
			else 
				return mockPromise({
					id: id,
					text: 'Wow you are so lucky that you have a turtle. I think the best name for a turtle is Bob, because Bob is the best name for everything.',
					timestamp: 1288323623006,
					voteCount: 0,
					hasVote: 0,
					answererId: 223,
					questionId: 692
				});
		}

		function getNewestQuestionsByUser(id) {
			return mockPromise([{

				id: 822,
				text: 'What is a good name for a pet turtle? Today I recieved a pet turtle and it is so amazing. I love it such much and it is cute. The only thing I do not know is what to name it. Help!',
				topics: [{
					name: 'turtles',
					id: 681
				}],
				voteCount: 20,
				askerId: id,
				hasVote: 1


			}]);
		}

		function getNewestAnswersByUser(id) {
			return mockPromise([{
				id: 555,
				text: 'Wow you are so lucky that you have a turtle. I think the best name for a turtle is Bob, because Bob is the best name for everything.',
				voteCount: 0,
				hasVote: 0,
				answererId: id,
				questionId: 811
			}]);
		}

		return {
			getNewestQuestions: getNewestQuestions,
			getExistingQuestions: getExistingQuestions,
			postNewUser: postNewUser,
			getUserById: getUserById,
			getTopicById: getTopicById,
			getLeaders: getLeaders,
			getQuestionById: getQuestionById,
			getHotTopics: getHotTopics,
			getAnswersByQuestionId: getAnswersByQuestionId,
			getAnswerById: getAnswerById,
			getNewestAnswersByUser: getNewestAnswersByUser,
			getNewestQuestionsByUser: getNewestQuestionsByUser
		};

	});


})(angular);