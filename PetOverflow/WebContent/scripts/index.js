(function (angular) {

	var app = angular.module('PetOverflow', ['ngRoute']);

	app.config(['$routeProvider', function($routeProvider) {

		$routeProvider
			.when('/', { templateUrl: 'pages/login.html' })
			.when('/questions/recent', { templateUrl: 'pages/questionList.html' })
			.when('/questions/existing', { templateUrl: 'pages/questionList.html' })
			.when('/users/new', { templateUrl: 'pages/signUp.html' })
			.when('/questions/ask', { templateUrl: 'pages/askQuestion.html'});

	}]);

	app.run(['$rootScope', '$location', function($rootScope, $location) {

		$rootScope
			.$on('loginSuccess', function () {
				$location.url('/questions/recent');
			});

	}]);


	app.factory('Session', ['$rootScope', '$http', function ($rootScope, $http) {

		var session = this;

		/**
			POSTS to the server. Emits loginSuccess on success.
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
					$rootScope.$emit('loginSuccess');
				}
				else {
					errCallback(data.message);
				}
			}).error(function () {
				alert('Error. Please contact system administrators or fix the damn code.');
			});
		}

		return {
			'authenticate': authenticate,
			'getUsername': function () {
				return this.username;
			}
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

	app.controller('QuestionsController', ['$rootScope', '$http', function ($rootScope, $http) {

		var questCtrl = this;

		questCtrl.newest = [];

		function init() {
			questCtrl.updateNewest();
		}

		questCtrl.updateNewest = function () {
			console.log("Updating the recent questions");
			$http.get('./questions/recent').then(function (response) {
				// resonse.data is an array of Questions objects (currently strings)
				// questCtrl.newest = response.data;
				questCtrl.newest = [{
						text: 'What is a good name for a pet turtle? Today I recieved a pet turtle and it is so amazing. I love it such much and it is cute. The only thing I do not know is what to name it. Help!',
						voteCount: 20,
						askerNickname: 'Bob',
						hasVote: 1
					}, {
						text: 'Why does my dog eat everything? In order to be clear, everything means: food, not food, animals, non-animals, leaves, humans, trumpets, and more.',
						voteCount: -1,
						askerNickname: 'Bobby',
						hasVote: -1
					}
				];
			});
		};

		init();

	}]);

	app.controller('QuestionAskingController', function() {

	});

	app.directive('petNav', function () {
		return {
			restrict: 'E',
			templateUrl: './pages/components/nav-bar.html'
		};
	});

	app.directive('question', function () {
		return {
			restrict: 'A',
			templateUrl: './pages/components/question.html'
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


}(angular));