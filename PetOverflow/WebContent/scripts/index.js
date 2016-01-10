(function (angular) {

	var app = angular.module('PetOverflow', ['ngRoute']);

	app.config(['$routeProvider', function($routeProvider) {

		$routeProvider
			.when('/', { templateUrl: 'pages/login.html' })
			.when('/questions/recent', { templateUrl: 'pages/recentQuestions.html' });

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
			POSTS to the server. Emits loginSucess on success.
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
		}

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
				questCtrl.newest = response.data;
			});
		};

		init();

	}]);


}(angular));