

(function (angular) {

	var app = angular.module('petOverflow', ['ngRoute', 'ngCookies', 'petd3', 'petData']);

	app.config(['$routeProvider', function($routeProvider) {

		$routeProvider
			.when('/', { templateUrl: 'pages/login.html' })
			.when('/questions/newest', { templateUrl: 'pages/browseQuestions.html' , controller: 'NewestQuestionsController as questCtrl'})
			.when('/questions/existing', { templateUrl: 'pages/browseQuestions.html' , controller: 'ExistingQuestionsController as questCtrl'})
			.when('/questions/ask', { templateUrl: 'pages/askQuestion.html'})
			.when('/questions/:qId', { 
				templateUrl: 'pages/questionView.html', 
				controller: 'QuestionController', 
				controllerAs: 'qCtrl' })
			.when('/questions/:qId/:mode', { 
				templateUrl: 'pages/questionView.html', 
				controller: 'QuestionController', 
				controllerAs: 'qCtrl' })
			.when('/users/new', { templateUrl: 'pages/signUp.html' })
			.when('/users/leading', { templateUrl: 'pages/leaderboard.html' })
			.when('/users/:uId', { templateUrl: 'pages/profile.html' })
			.when('/topics/hot', { templateUrl: 'pages/hotTopics.html'})
			.when('/topics/:tId', { templateUrl: 'pages/topic.html' })
			.otherwise({ templateUrl: 'pages/404.html' });

	}]);

	app.run(['$rootScope', '$location', 'DEFAULT_PROFILE', function($rootScope, $location, DEFAULT_PROFILE) {

		$rootScope.$on('login', function () {
			$location.url('/questions/newest');
		})
		$rootScope.$on('logout', function () {
			$location.url('/');
		});

		$rootScope.contants = {
			DEFAULT_PROFILE: DEFAULT_PROFILE
		};

	}]);


	app.factory('Session', ['$rootScope', '$http', '$cookies', function ($rootScope, $http, $cookies) {

		var session = this;

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
				data: data,
				headers: {'Content-Type': 'application/x-www-form-urlencoded'}
			}).success(function (data) {
				if (data.success) {
					session.username = username;
					$rootScope.$emit('login');
				}
				else {
					errCallback(data.message);
				}
			}).error(function () {
				alert('Error. Please contact system administrators or fix the damn code.');
			});
		}

		function logout() {
			$cookies.remove('username');
			$cookies.remove('password');
			$rootScope.$emit('logout');
		}

		return {
			'authenticate': authenticate,
			'getUsername': function () {
				return this.username;
			},
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

	app.controller('NewestQuestionsController', ['$scope', 'PetData', 'HttpFailHandler', function ($scope, PetData, HttpFailHandler) {

		var questCtrl = this;
		questCtrl.newest = [];

		function init() {
			questCtrl.updateNewest();
		}

		questCtrl.updateNewest = function () {
			PetData.getNewestQuestions().then(
				function (response) {
					questCtrl.newest = response.data;
				},
				HttpFailHandler
			);
		};

		init();

	}]);

	app.controller('ExistingQuestionsController', ['PetData', 'HttpFailHandler', function (PetData, HttpFailHandler) {

		var questCtrl = this;

		questCtrl.existing = [];

		function init() {
			questCtrl.updateExisting();
		}

		questCtrl.updateExisting = function () {
			PetData.getExistingQuestions().then(
				function (response) {
					questCtrl.existing = response.data;
				},
				HttpFailHandler
			);
		};

		init();

	}]);


	app.controller('SignUpController', ['PetData', '$rootScope', function (PetData, $rootScope) {

		this.wantsSms = false;
		this.photoUrl = '';

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

			PetData.postNewUser(userInfo).then(function (response) {
				$rootScope.$emit('login');	
			}, function (response) {
				// Sign up failed
			});
			
		};


	}]);

	app.controller('QuestionAskingController', ['$scope', function ($scope) {

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
			else {
				this.topics.push(this.newTopic);
				this.newTopic = '';
				$scope.$broadcast('topicAdded');
			}
		};

	}]);

	app.controller('ProfileController', ['$routeParams', 'Session', 'PetData', 'HttpFailHandler', 'DEFAULT_PROFILE', function ($routeParams, Session, PetData, HttpFailHandler, DEFAULT_PROFILE) {
		var prCtrl = this;
		prCtrl.user = {};
		prCtrl.newestQuestions = [];
		prCtrl.newestAnswers = [];
		prCtrl.DEFAULT_PROFILE = DEFAULT_PROFILE;

		var userId;
		if ($routeParams.uId == 'me') userId = Session.userId;
		else userId = $routeParams.uId;		
		
		PetData.getUserById(userId).then(function (response) {
			prCtrl.user = response.data;
		}, HttpFailHandler);

		PetData.getNewestQuestionsByUser(userId).then(function (response) {
			prCtrl.newestQuestions = response.data;
		}, HttpFailHandler);

		PetData.getNewestAnswersByUser(userId).then(function (response) {
			prCtrl.newestAnswers = response.data;
		}, HttpFailHandler);

	}]);

	app.controller('NavBarController', ['Session', function (Session) {
		this.logout = Session.logout;
	}]);

	app.controller('HotTopicsController', ['PetData', 'HttpFailHandler', function (PetData, HttpFailHandler) {
		var htpCtrl = this;
		htpCtrl.topics = [];

		PetData.getHotTopics().then(function (response) {
			htpCtrl.topics = response.data;
		}, HttpFailHandler);
	}]);

	app.controller('TopicController', ['PetData', 'HttpFailHandler', '$routeParams', function (PetData, HttpFailHandler, $routeParams) {
		var tpCtrl = this;
		tpCtrl.name = '';
		tpCtrl.questions = [];

		PetData.getTopicById($routeParams.tId).then(function (response) {
			tpCtrl.name = response.data.name;
			tpCtrl.questions = response.data.questions;
		}, HttpFailHandler);

	}]);

	app.controller('LeaderboardController', ['PetData', 'HttpFailHandler', function (PetData, HttpFailHandler) {		
		var lbCtrl = this;
		lbCtrl.leaders = [];

		lbCtrl.init = function () {
			PetData.getLeaders().then(function (response) {
				lbCtrl.leaders = response.data;
				lbCtrl.initGraphData();
			}, HttpFailHandler);

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
		

	    lbCtrl.initGraphData = function (){
	    	lbCtrl.graphData = [{
			    values: lbCtrl.leaders.map(function (leader) {
			    	return { label: leader.nickname, value: leader.rating };
			    })
			}];
		};

		lbCtrl.init();
	}]);

	app.controller('QuestionController', ['$timeout', '$scope', '$routeParams', 'PetData', 'HttpFailHandler', function ($timeout, $scope, $routeParams, PetData, HttpFailHandler) {
		var qCtrl = this;
		qCtrl.qId = $routeParams.qId;

		PetData.getAnswersByQuestionId(qCtrl.qId).then(function (response) {
			qCtrl.answers = response.data;
		}, HttpFailHandler);

		$timeout(function () {
			if ($routeParams.mode == 'answer') {
				$scope.$emit('answerRequest');
				window.setTimeout(function() {$scope.$emit('answerRequest');}, 30);
			}
		}, 1000);

		
	}]);

	app.directive('userBox', ['PetData', 'HttpFailHandler', 'DEFAULT_PROFILE', function (PetData, HttpFailHandler, DEFAULT_PROFILE) {
		return {
			restrict: 'E',
			scope: {
				userId: '='
			},
			link: function (scope, element, attr) {
				PetData.getUserById(scope.userId).then(function (response) {
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

	app.directive('question', ['PetData', 'HttpFailHandler', function (PetData, HttpFailHandler) {
		return {
			restrict: 'E',
			templateUrl: './pages/components/question.html',
			scope: {
				id: '=questionId',
				withAnswer: '=',
				fullPageMode: '=fullPage'
			},
			link: function (scope, element, attrs, controller) {
				PetData.getQuestionById(scope.id).then(function (response) {
					scope.question = response.data;
				}, HttpFailHandler);

				if (scope.withAnswer)
					element[0].querySelector('.Question')
						.classList.add('Question--withAnswer');
			}
		};
	}]);

	app.directive('answer', ['PetData', 'HttpFailHandler', function (PetData, HttpFailHandler) {
		return {
			restrict: 'E',
			templateUrl: './pages/components/answer.html',
			scope: {
				id: '=answerId',
				withQuestion: '='
			},
			link: function (scope, element, attrs, controller) {
				PetData.getAnswerById(scope.id).then(function (response) {
					scope.answer = response.data;
				}, HttpFailHandler);
				
				if (scope.withQuestion)
					element[0].querySelector('.Answer')
						.classList.add('Answer--withQuestion');
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
		        	elem[0].focus();
		    	});
		    }
	   };
	});

	app.directive('onEnterKey', function() {
	    return  {
	    	restrict: 'A',
	    	link: function(scope, element, attrs) {

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


}(angular));
