

(function (angular, $) {

	var app = angular.module('petOverflow', ['ngRoute', 'ngCookies', 'petd3', 'petData']);

	app.config(['$routeProvider', function($routeProvider) {

		$routeProvider
			.when('/', { templateUrl: 'pages/login.html' })
			.when('/questions/browse/:type', { 
				templateUrl: 'pages/browseQuestions.html' , 
				controller: 'BrowseQuestionsController as questCtrl'})
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
			$location.url('/questions/browse/newest');
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

	app.controller('BrowseQuestionsController', ['$scope', 'PetData', 'HttpFailHandler', '$routeParams', function ($scope, PetData, HttpFailHandler, $routeParams) {

		var questCtrl = this;
		questCtrl.questions = [];
		questCtrl.currentPage = 0;
		questCtrl.PAGE_SIZE = 10;
		questCtrl.type = $routeParams.type;

		function init() {
			questCtrl.updateQuestions();
		}

		questCtrl.firstIndex = function () {
			return questCtrl.currentPage * questCtrl.PAGE_SIZE + 1;
		}
		questCtrl.lastIndex = function () {
			return questCtrl.firstIndex() + questCtrl.questions.length - 1;
		}

		questCtrl.updateQuestions = function () {
			var updater;
			if (questCtrl.type === 'existing') updater = PetData.getExistingQuestions;
			else if (questCtrl.type === 'newest') updater = PetData.getNewestQuestions;

			updater(this.firstIndex(), this.lastIndex()).then(
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

		questCtrl.previousPage = function () {
			if (questCtrl.currentPage > 0)
				questCtrl.currentPage -= 1;
			questCtrl.updateQuestions();
		};
		questCtrl.nextPage = function () {
			questCtrl.currentPage += 1;
			questCtrl.updateQuestions();
		};
		$scope.$on('previousPage', questCtrl.previousPage);
		$scope.$on('nextPage', questCtrl.nextPage);

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

			PetData.postUser(userInfo).then(function (response) {
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
		
		PetData.getUser(userId).then(function (response) {
			prCtrl.user = response.data;
		}, HttpFailHandler);

		PetData.getUserQuestions(userId).then(function (response) {
			prCtrl.newestQuestions = response.data;
		}, HttpFailHandler);

		PetData.getUserAnswers(userId).then(function (response) {
			prCtrl.newestAnswers = response.data;
		}, HttpFailHandler);

	}]);

	app.controller('NavBarController', ['Session', function (Session) {
		this.logout = Session.logout;
	}]);

	app.controller('HotTopicsController', ['PetData', 'HttpFailHandler', function (PetData, HttpFailHandler) {
		var htpCtrl = this;
		htpCtrl.topics = [];

		PetData.getTopicsPopular().then(function (response) {
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
			PetData.getUsersLeaders(this.firstIndex(), this.lastIndex()).then(function (response) {
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
			return lbCtrl.currentPage * lbCtrl.PAGE_SIZE + 1;
		}
		lbCtrl.lastIndex = function () {
			return lbCtrl.firstIndex() + lbCtrl.leaders.length - 1;
		}
		lbCtrl.previousPage = function () {
			if (lbCtrl.currentPage > 0)
				lbCtrl.currentPage -= 1;
			lbCtrl.updateData();
		};
		lbCtrl.nextPage = function () {
			lbCtrl.currentPage += 1;
			lbCtrl.updateData();
		};
		$scope.$on('previousPage', lbCtrl.previousPage);
		$scope.$on('nextPage', lbCtrl.nextPage);

		lbCtrl.init();
	}]);

	app.controller('QuestionController', ['$timeout', '$scope', '$routeParams', 'PetData', 'HttpFailHandler', function ($timeout, $scope, $routeParams, PetData, HttpFailHandler) {
		var qCtrl = this;
		qCtrl.qId = $routeParams.qId;

		PetData.getQuestionAnswers(qCtrl.qId).then(function (response) {
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

	app.directive('question', ['PetData', 'HttpFailHandler', function (PetData, HttpFailHandler) {
		return {
			restrict: 'E',
			templateUrl: './pages/components/question.html',
			scope: {
				id: '=questionId',
				fullPageMode: '=fullPage',
				withAnswer: '='
			},
			link: function (scope, element, attrs, controller) {
				PetData.getQuestion(scope.id).then(function (response) {
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
				PetData.getAnswer(scope.id).then(function (response) {
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
