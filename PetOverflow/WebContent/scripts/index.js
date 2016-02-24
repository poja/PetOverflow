

(function (angular, $) {

	var app = angular.module('petOverflow', ['petd3', 'ngRoute', 'petData']);

	app.config(['$routeProvider', function($routeProvider) {

		$routeProvider
			.when('/', { templateUrl: 'pages/login.html' })
			.when('/questions/browse/:type', { 
				templateUrl: 'pages/browseQuestions.html' , 
				controller: 'BrowseQuestionsController as questCtrl'})
			.when('/questions/search', { templateUrl: 'pages/questionSearch.html' })
			.when('/questions/ask', { templateUrl: 'pages/askQuestion.html'})
			.when('/questions/:qId', { templateUrl: 'pages/questionView.html' })
			.when('/questions/:qId/:mode', { templateUrl: 'pages/questionView.html' })
			.when('/users/new', { templateUrl: 'pages/signUp.html' })
			.when('/users/leading', { templateUrl: 'pages/leaderboard.html' })
			.when('/users/:uId', { templateUrl: 'pages/profile.html' })
			.when('/topics/hot', { templateUrl: 'pages/hotTopics.html'})
			.when('/topics/:tName', { templateUrl: 'pages/topic.html' })
			.when('/settings', { templateUrl: 'pages/settings.html' })
			.otherwise({ templateUrl: 'pages/404.html' });

	}]);

	app.run(['$rootScope', '$location', 'DEFAULT_PROFILE', function($rootScope, $location, DEFAULT_PROFILE) {

		$rootScope.$on('login', function () {
			$location.url('/questions/browse/newest');
		})
		$rootScope.$on('logout', function () {
			$location.url('/');
		});
		$rootScope.$on('questionPosted', function () {
			$location.url('/questions/browse/newest');
		});
		$rootScope.$on('settingsSaved', function () {
			$location.url('/questions/browse/newest');
		});

		$rootScope.contants = {
			DEFAULT_PROFILE: DEFAULT_PROFILE
		};

	}]);

	app.factory('$localStorage', ['$window', function ($window) {
		return $window.localStorage;
	}]);

	app.factory('Session', ['$rootScope', '$http', '$localStorage', function ($rootScope, $http, $localStorage) {

		/**
			POSTS to the server. Emits 'login' on success.
			On error calls back with a message from the server.
		*/
		function authenticate(username, password, errCallback) {
			var data = {
				'username': username,
				'password': password
			};
			
			$http({
				method: 'POST',
				url: './login',
				data: data
			}).success(function (data) {
				if (data.success) {
					$localStorage.setItem('username', data.username);
					$localStorage.setItem('userId', data.id);
					$rootScope.$emit('login');
				}
				else {
					errCallback(data.message);
				}
			}).error(function () {
				alert('Error. Please contact system administrators or fix the damn code.');
			});
		}

		function getUsername() {
			return $localStorage.getItem('username');
		}
		function getUserId() {
			return $localStorage.getItem('userId');
		}

		function logout() {
			$http({
				method: 'POST',
				url: './logout'
			}).then(function (response) {
				$localStorage.removeItem('username');
				$localStorage.removeItem('userId');
				$rootScope.$emit('logout');
			});
		}

		return {
			'authenticate': authenticate,
			'getUsername': getUsername,
			'getUserId': getUserId,
			'logout': logout
		};

	}]);


	app.controller('LoginController', ['Session', function (Session) {
		var loginCtrl = this;

		loginCtrl.login = function (isValid) {
			if (!isValid) {
				this.errMessage = 'Some of the form values are empty or invalid.';
				return false;
			}
			
			Session.authenticate(loginCtrl.username, loginCtrl.password, function (err) {
				loginCtrl.errMessage = err;
			});
		};

	}]);

	app.controller('BrowseQuestionsController', ['$scope', 'PetData', 'HttpFailHandler', '$routeParams', function ($scope, PetData, HttpFailHandler, $routeParams) {

		var questCtrl = this;
		questCtrl.questions = [];
		questCtrl.currentPage = 0;
		questCtrl.PAGE_SIZE = 20;
		questCtrl.type = $routeParams.type;

		function init() {
			questCtrl.updateQuestions();
		}

		questCtrl.updateQuestions = function () {
			var getQuestions;
			if (questCtrl.type === 'existing') getQuestions = PetData.getQuestionsExisting;
			else if (questCtrl.type === 'newest') getQuestions = PetData.getQuestionsNewest;

			getQuestions(this.PAGE_SIZE, this.firstIndex()).then(
				function (response) {
					if (response.data.length == 0 && questCtrl.currentPage > 0) { // Went too far
						questCtrl.previousPage();
						return;
					}
					questCtrl.questions = response.data;
				},
				HttpFailHandler
			);
		};

		questCtrl.firstIndex = function () {
			return questCtrl.currentPage * questCtrl.PAGE_SIZE;
		}
		questCtrl.lastIndex = function () {
			return questCtrl.firstIndex() + questCtrl.questions.length - 1;
		}

		questCtrl.previousPage = function () {
			if (questCtrl.currentPage > 0) {
				questCtrl.currentPage -= 1;
				questCtrl.updateQuestions();
			}
		};
		questCtrl.nextPage = function () {
			if (questCtrl.questions.length >= questCtrl.PAGE_SIZE) {
				questCtrl.currentPage += 1;
				questCtrl.updateQuestions();
			}
		};
		$scope.$on('previousPage', questCtrl.previousPage);
		$scope.$on('nextPage', questCtrl.nextPage);

		init();

	}]);


	app.controller('SignUpController', ['$scope', '$localStorage', 'PetData', '$rootScope', function ($scope, $localStorage, PetData, $rootScope) {

		this.wantsSms = false;
		this.photoUrl = '';
		this.description = '';

		this.submit = function() {
			var userInfo = {
				username: this.username,
				password: this.password,
				nickname: this.nickname,
				description: this.description,
				photoUrl: this.photoUrl,
				phoneNumber: this.phoneNumber,
				wantsSms: this.wantsSms
			};

			PetData.postUser(userInfo).then(function (response) {
				if (response.data.errorMessage)
					alert(response.data.errorMessage);
				else {
					$localStorage.setItem('username', response.data.username);
					$localStorage.setItem('userId', response.data.id);
					$rootScope.$emit('login');	
				}
			});
		};
	}]);

	app.controller('QuestionAskingController', ['PetData', '$scope', function (PetData, $scope) {

		var TOPIC_REGEX = /^[a-zA-Z0-9]+$/,
			MAX_TOPIC_LENGTH = 50;

		this.topics = [];

		this.addTopic = function () {
			if (!this.newTopic) {
				this.topicError = 'Please enter a name for the topic';
			}
			else if (this.newTopic.length > MAX_TOPIC_LENGTH)
				this.topicError = 'The name of the topic should be less than 50 characters';
			else if (!this.newTopic.match(TOPIC_REGEX))
				this.topicError = 'Topic name should only have digits and letters';
			else if (this.topics.includes(this.newTopic))
				this.topicError = 'You have already added this topic';
			else {
				this.topics.push(this.newTopic);
				this.newTopic = '';
				$scope.$broadcast('topicAdded');
			}
		};

		this.removeTopic = function (topicIndex) {
			this.topics.splice(topicIndex, 1);
		};

		this.submitQuestion = function () {
			PetData.postQuestion(this.text, this.topics).then(function () {
				$scope.$emit('questionPosted');
			});
		};

	}]);

	app.controller('ProfileController', ['$routeParams', 'Session', 'PetData', 'HttpFailHandler', 'DEFAULT_PROFILE', function ($routeParams, Session, PetData, HttpFailHandler, DEFAULT_PROFILE) {
		var prCtrl = this;
		prCtrl.user = {};
		prCtrl.newestQuestions = [];
		prCtrl.newestAnswers = [];
		prCtrl.DEFAULT_PROFILE = DEFAULT_PROFILE;

		var DISPLAY_SIZE = 5; // the number of questions / answers to display

		var userId;
		if ($routeParams.uId == 'me') userId = Session.getUserId();
		else userId = $routeParams.uId;		
		
		PetData.getUser(userId).then(function (response) {
			prCtrl.user = response.data;
		}, HttpFailHandler);

		PetData.getUserQuestions(userId, DISPLAY_SIZE, 0).then(function (response) {
			prCtrl.newestQuestions = response.data;
		}, HttpFailHandler);

		PetData.getUserAnswers(userId, DISPLAY_SIZE, 0).then(function (response) {
			prCtrl.newestAnswers = response.data;
		}, HttpFailHandler);

	}]);

	app.controller('NavBarController', ['Session', function (Session) {
		this.logout = Session.logout;
	}]);

	app.controller('HotTopicsController', ['PetData', 'HttpFailHandler', function (PetData, HttpFailHandler) {
		var htpCtrl = this;
		htpCtrl.hotTopics = [];
		htpCtrl.searchedTopics = [];
		htpCtrl.cloudData = [];
		htpCtrl.query = '';

		var MAX_CLOUD_SIZE = 60;
		var MAX_SEARCH_SIZE = 20;

		PetData.getTopicsPopular(MAX_CLOUD_SIZE, 0).then(function (response) {
			htpCtrl.hotTopics = response.data;
			htpCtrl.cloudData = htpCtrl.hotTopics.map(function (topic) {
				return {
					text: topic.name,
					size: topic.rating,
					url: '#/topics/' + topic.name
				}
			});
		}, HttpFailHandler);

		htpCtrl.search = function () {
			PetData.getTopicsByText(htpCtrl.query, MAX_SEARCH_SIZE, 0).then(function (reponse) {
				this.searchedTopics = response.data;
			});
		};
	}]);

	app.controller('TopicController', ['$scope', 'PetData', 'HttpFailHandler', '$routeParams', function ($scope, PetData, HttpFailHandler, $routeParams) {
		var tpCtrl = this;
		tpCtrl.name = $routeParams.tName;
		tpCtrl.questions = [];
		tpCtrl.currentPage = 0;
		tpCtrl.PAGE_SIZE = 20;

		tpCtrl.updateQuestions = function () {
			PetData.getTopicQuestions(tpCtrl.name, 20, 0).then(function (response) {
				tpCtrl.questions = response.data;
			}, HttpFailHandler);
		};

		tpCtrl.firstIndex = function () {
			return tpCtrl.currentPage * tpCtrl.PAGE_SIZE;
		}
		tpCtrl.lastIndex = function () {
			return tpCtrl.firstIndex() + tpCtrl.questions.length - 1;
		}

		tpCtrl.previousPage = function () {
			if (tpCtrl.currentPage > 0) {
				tpCtrl.currentPage -= 1;
				tpCtrl.updateQuestions();
			}
		};
		tpCtrl.nextPage = function () {
			if (tpCtrl.questions.length >= tpCtrl.PAGE_SIZE) {
				tpCtrl.currentPage += 1;
				tpCtrl.updateQuestions();
			}
		};
		$scope.$on('previousPage', tpCtrl.previousPage);
		$scope.$on('nextPage', tpCtrl.nextPage);
		
		tpCtrl.updateQuestions();
	}]);

	app.controller('LeaderboardController', ['$scope', 'PetData', 'HttpFailHandler', function ($scope, PetData, HttpFailHandler) {		
		var lbCtrl = this;
		lbCtrl.leaders = [];
		lbCtrl.currentPage = 0;
		lbCtrl.PAGE_SIZE = 20;

		lbCtrl.init = function () {
			lbCtrl.initGraphOptions();
			lbCtrl.updateData();
		}

		lbCtrl.updateData = function () {
			PetData.getUsersLeaders(this.PAGE_SIZE, this.firstIndex()).then(function (response) {
				if (response.data.length === 0 && lbCtrl.currentPage > 0) {
					lbCtrl.previousPage();
					return;
				}
				lbCtrl.leaders = response.data;
				lbCtrl.updateGraphData();
			}, HttpFailHandler);
		}

	    lbCtrl.updateGraphData = function (){
	    	lbCtrl.graphData = [{
			    values: lbCtrl.leaders.map(function (leader) {
			    	return { label: leader.nickname, value: leader.rating };
			    })
			}];
		};

		lbCtrl.initGraphOptions = function () {
			lbCtrl.graphOptions = {
				chart: {
			        type: 'discreteBarChart',
			        height: 450,
			        margin : {
			            top: 20,
			            right: 20,
			            bottom: 60,
			            left: 55
			        },
			        x: function(d){ return d.label; },
			        y: function(d){ return d.value; },
			        showValues: true,
			        valueFormat: function(d){
			            return d3.format(',.4f')(d);
			        },
			        transitionDuration: 500,
			        xAxis: {
			            axisLabel: 'Leaders'
			        },
			        yAxis: {
			            axisLabel: 'Rating',
			            axisLabelDistance: 30
		        	}
		        }
		    };	
		}

		lbCtrl.firstIndex = function () {
			return lbCtrl.currentPage * lbCtrl.PAGE_SIZE;
		}
		lbCtrl.lastIndex = function () {
			return lbCtrl.firstIndex() + lbCtrl.leaders.length - 1;
		}
		lbCtrl.previousPage = function () {
			if (lbCtrl.currentPage > 0) {
				lbCtrl.currentPage -= 1;
				lbCtrl.updateData();
			}
		};
		lbCtrl.nextPage = function () {
			if (lbCtrl.leaders.length >= lbCtrl.PAGE_SIZE) {
				lbCtrl.currentPage += 1;
				lbCtrl.updateData();
			}
		};
		$scope.$on('previousPage', lbCtrl.previousPage);
		$scope.$on('nextPage', lbCtrl.nextPage);

		lbCtrl.init();
	}]);

	app.controller('QuestionController', ['$timeout', '$scope', '$routeParams', 'PetData', 'HttpFailHandler', function ($timeout, $scope, $routeParams, PetData, HttpFailHandler) {
		var qCtrl = this;
		qCtrl.qId = $routeParams.qId;
		qCtrl.currentPage = 0;
		qCtrl.PAGE_SIZE = 20;
		qCtrl.answers = [];

		function init() {
			qCtrl.updateAnswers();

			$timeout(function () {
				if ($routeParams.mode == 'answer')
					$scope.$emit('answerRequest');
			}, 1000);	
		}

		qCtrl.updateAnswers = function () {
			PetData.getQuestionAnswers(qCtrl.qId, qCtrl.PAGE_SIZE, qCtrl.firstIndex()).then(function (response) {
				if (response.data.length === 0 && qCtrl.currentPage > 0) {
					qCtrl.previousPage();
					return;
				}
				qCtrl.answers = response.data;
			}, HttpFailHandler);	
		}

		qCtrl.postAnswer = function () {
			PetData.postAnswer(qCtrl.newAnswer, Number(qCtrl.qId)).then(function (response) {
				qCtrl.answers.push(response.data);
				qCtrl.newAnswer = '';
			});
		};

		qCtrl.firstIndex = function () {
			return qCtrl.currentPage * qCtrl.PAGE_SIZE;
		}
		qCtrl.lastIndex = function () {
			return qCtrl.firstIndex() + qCtrl.answers.length - 1;
		}
		qCtrl.previousPage = function () {
			if (qCtrl.currentPage > 0) {
				qCtrl.currentPage -= 1;
				qCtrl.updateData();
			}
		};
		qCtrl.nextPage = function () {
			if (qCtrl.answers.length >= qCtrl.PAGE_SIZE) {
				qCtrl.currentPage += 1;
				qCtrl.updateData();
			}
		};
		$scope.$on('previousPage', qCtrl.previousPage);
		$scope.$on('nextPage', qCtrl.nextPage);

		init();
	}]);

	app.controller('SettingsController', ['$scope', 'PetData', 'Session', 'HttpFailHandler', function ($scope, PetData, Session, HttpFailHandler) {
		var stngCtrl = this;

		PetData.getUser(Session.getUserId()).then(function (response) {
			stngCtrl.userInfo = response.data;
		}, HttpFailHandler);

		stngCtrl.submit = function () {
			var userInfo = stngCtrl.userInfo;
			var unfinished = 4;
			PetData.putUserDescription(userInfo).then(function () {
				unfinished -= 1;
				if (!unfinished) $scope.$emit('settingsSaved');
			});
			PetData.putUserWantsSms(userInfo).then(function () {
				unfinished -= 1;
				if (!unfinished) $scope.$emit('settingsSaved');
			});
			PetData.putUserPhoto(userInfo).then(function () {
				unfinished -= 1;
				if (!unfinished) $scope.$emit('settingsSaved');
			});
			PetData.putUserPhone(userInfo).then(function () {
				unfinished -= 1;
				if (!unfinished) $scope.$emit('settingsSaved');
			});
		};

	}]);

	app.controller('QuestionSearchController', ['PetData', function (PetData) {
		var srchCtrl = this;
		srchCtrl.questions = [];
		srchCtrl.MAX_SIZE = 20;

		srchCtrl.search = function () {
			PetData.getQuestionsByText(srchCtrl.query, srchCtrl.MAX_SIZE, 0).then(function (response) {
				srchCtrl.questions = response.data;
			});
		};
	}]);

	app.directive('userBox', ['PetData', 'HttpFailHandler', 'DEFAULT_PROFILE', function (PetData, HttpFailHandler, DEFAULT_PROFILE) {
		return {
			restrict: 'E',
			scope: {
				userId: '=userId'
			},
			link: function (scope, element, attr) {
				PetData.getUser(scope.userId).then(function (response) {
					scope.user = response.data;
					scope.DEFAULT_PROFILE = DEFAULT_PROFILE;
				}, HttpFailHandler);
			},
			templateUrl: './pages/components/user-box.html'
		};
	}]);

	app.directive('petNav', function () {
		return {
			restrict: 'E',
			templateUrl: './pages/components/nav-bar.html'
		};
	});
	app.directive('questionsNav', function () {
		return {
			restrict: 'E',
			templateUrl: './pages/components/questions-nav.html'
		};
	});

	app.directive('question', ['Session', 'PetData', 'HttpFailHandler', function (Session, PetData, HttpFailHandler) {
		return {
			restrict: 'E',
			templateUrl: './pages/components/question.html',
			scope: {
				id: '=questionId',
				fullPageMode: '=fullPage',
				withAnswer: '='
			},
			link: function (scope, element, attrs, controller) {
				function updateInfo() {
					PetData.getQuestion(scope.id).then(function (response) {
						scope.questionInfo = response.data;
						if (scope.questionInfo.authorId == Session.getUserId())
							scope.isMyQuestion = true;
					}, HttpFailHandler);
				}
				scope.setVote = function (newStatus) {
					if (scope.isMyQuestion)
						console.log('You cannot vote your own item, silly!');
					else
						PetData.putQuestionVote(scope.id, newStatus).then(function (data) {
							updateInfo();
						});	
				};
				
				scope.isMyQuestion = false;
				updateInfo();
			}
		};
	}]);

	app.directive('answer', ['Session', 'PetData', 'HttpFailHandler', function (Session, PetData, HttpFailHandler) {
		return {
			restrict: 'E',
			templateUrl: './pages/components/answer.html',
			scope: {
				id: '=answerId',
				withQuestion: '='
			},
			link: function (scope, element, attrs, controller) {
				function updateInfo() {
					PetData.getAnswer(scope.id).then(function (response) {
						scope.answerInfo = response.data;
						if (scope.answerInfo.authorId == Session.getUserId())
							scope.isMyAnswer = true;
					}, HttpFailHandler);
				}
				scope.setVote = function (newStatus) {
					if (scope.isMyAnswer)
						console.log('You cannot vote your own item, silly!');
					else 
						PetData.putAnswerVote(scope.id, newStatus).then(function () {
							updateInfo();
						});
				}
				
				scope.isMyAnswer = false;
				updateInfo();
			}
		};
	}]);

	app.directive('leader', function () {
		return {
			restrict: 'A',
			templateUrl: './pages/components/leader.html'
		};
	});


	app.directive('activeLink', ['$location', function (location) {
	    return {
	    	restrict: 'A',
	    	link: function (scope, element, attrs, controller) {
		        var clazz = attrs.activeLink;
		        var path = attrs.href;
		        path = path.substring(1); //hack because path does not return including hashbang
		        scope.location = location;
		        scope.$watch('location.path()', function (newPath) {
		        	if (path === newPath) {
		            	element.addClass(clazz);
		          	} else {
		            	element.removeClass(clazz);
		          	}
		        });
	    	}
	    };
	}]);

	app.directive('focusOn', function() {
		return {
	   		restrict: 'A',
	   		link: function(scope, elem, attr) {
		    	scope.$on(attr.focusOn, function(e) {
		    		if (attr.scrollTime) {
		    			// Smooth scroll then focus
		    			$('html, body').animate({
				            scrollTop: $(elem).offset().top
				        }, attr.scrollTime, 'swing', function () {
				        	elem[0].focus();	
				        });
		    		}
		    		else
		    			// Just focus
		        		elem[0].focus();
		    	});
		    }
	   };
	});

	app.directive('onEnterKey', function() {
	    return  {
	    	restrict: 'A',
	    	link: function (scope, element, attrs) {

		        element.bind("keydown keypress", function(event) {
		            var keyCode = event.which || event.keyCode;

		            // If enter key is pressed
		            if (keyCode === 13) {
		                scope.$apply(function() {
		                        // Evaluate the expression
		                    scope.$eval(attrs.onEnterKey);
		                });

		                event.preventDefault();
		            }
		        });
		    }
		};
	});

	app.directive('pageTurner', function () {
		return {
			restrict: 'E',
			templateUrl: './pages/components/page-turner.html',
			scope: {
				text: '='
			},
			link: function (scope, element, attr) {
				scope.nextPage = function () {
					scope.$emit('nextPage');
				}
				scope.previousPage = function () {
					scope.$emit('previousPage');
				}
			}
		};
	});

	app.filter('arrayPrint', function () {
		return function (array) {
			
			if (!array || !(array instanceof Array)) return null;

			var string = array.reduce(function (oldOutcome, currentValue) {
				return oldOutcome + ', ' + currentValue;
			}, '').substring(2);

			return string;
		};
	});

	app.constant('HttpCodes', {
		OK: 200,
		UNAUTHORIZED: 401,
		BAD_REQUEST: 400,
		INTERNAL_SERVER_ERROR: 500,
		FORBIDDEN: 403
	});

	app.constant('DEFAULT_PROFILE', 'https://www.mybeacon.biz/static/img/anonymous.jpg');

	/**
	 * Default failure handler for http requests.
	 * If the reason is because the user was unauthorized, the user is notified and led to the login screen.
	 */
	app.factory('HttpFailHandler', ['HttpCodes', '$window', '$rootScope', function (HttpCodes, $window, $rootScope) {
		return function (response) {
			if (response.status ==  HttpCodes.UNAUTHORIZED) {
				$window.alert('You are not authorized to view this page, and therefore are redirected to the login page.');
				$rootScope.$emit('logout');
			}
		}
	}]);


}(angular, jQuery));
