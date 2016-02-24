/**
 * This Angular module implements the basic API of ngRoute 
 * 
 * Includes ng-view, $routeProvider, and $routeParams
 */
 (function (angular) {

 	var petRoute = angular.module('petRoute', []);

 	petRoute.provider('$route', function () {
 		
 		/**
		 * Config is has 'when' and 'otherwise'
		 * - 'when' is an array of objects with url, and settings
		 * - 'otherwise' is settings when no url is matched
		 *
		 * url can have /:paramName/ , which is a wildcard that will
		 * 		appear in $routeParams
		 * The **first** matching rule is taken
		 * 
		 * settings can have 
		 * - templateUrl
		 * - template (overrides templateUrl)
		 * - parameters dictionary(addition to original ngRoute)
 		 */
 		var config = {
 			when: [],
 			otherwise: {}
 		};

 		var routeParams = {};   // singleton object, passed later at $routeParams

 		function getRouteParams() {
 			return routeParams;
 		}

 		function when(url, settings) {
 			config.when.push({
 				url: url,
 				settings: settings
 			});
 			return this;
 		}

 		function otherwise(settings) {
 			config.otherwise = settings;
 		}

 		function getSettings(hashLocation) {
 			var settings = config.otherwise;
 			
 			config.when.every(function (option) {
 				var params = {};
 				var urlFormat = option.url;

 				var i = 0, j = 0;
 				while (i < hashLocation.length && j < urlFormat.length) {
 					if (urlFormat[j] === ':') {
 						j++;
 						var part = '';
 						while (hashLocation[i] && hashLocation[i] != '/') {
 							part += hashLocation[i];
 							i++;
 						}
 						var partName = '';
 						while (urlFormat[j] && urlFormat[j] != '/') {
 							partName += urlFormat[j];
 							j++;
 						}
 						params[partName] = part;
 					}
 					else {
 						if (hashLocation[i] != urlFormat[j])
 							return true; // no match. continue for loop to find a different match
 						i++;
 						j++;
 					}
 				}

 				// Allow an extra / at the end of eiter one of them
 				if (hashLocation[i] === '/') i++;
 				if (urlFormat[j] === '/') j++;

 				if (i == hashLocation.length && j == urlFormat.length) {
 					// The two strings matched!!
 					settings = {
 						templateUrl: option.settings.templateUrl,
 						template: option.settings.template,
 						parameters: params
 					};
 					clearObject(routeParams);
 					for (var attrName in params)
 						routeParams[attrName] = params[attrName];
 					for (var attrName in option.settings.parameters)
 						routeParams[attrName] = option.settings.parameters[attrName];
 					return false;
 				}
 				// No match. Keep looking.
 				return true;
 			});

 			return settings;
 		}

 		this.when = when;
 		this.otherwise = otherwise;
 		this.$get = function (){
 			return {
 				getSettings: getSettings,
 				getRouteParams: getRouteParams
 			};
 		};
 	});

 	petRoute.factory('$routeParams', ['$route', function ($route, $window) {
 		return $route.getRouteParams();
 	}]);

 	petRoute.directive('ngView', ['$window', '$route', '$http', '$compile', function ($window, $route, $http, $compile) {
 		return {
 			restrict: 'AE',
 			link: function (scope, element, attrs, controller) {

 				function insertTemplate(templateHtml) {
 					element.html(templateHtml);
      				$compile(element.contents())(scope);
 				}
 				function updateContent () {
 					var hashLocation = $window.location.hash.replace('#', '');
 					var settings = $route.getSettings(hashLocation);

 					if (settings.template)
 						insertTemplate(settings.template);
 					else {
 						$http.get(settings.templateUrl).then(function (response) {
 							settings.template = response.data;
 							insertTemplate(settings.template);
 						});
 					}
 				}

 				$window.addEventListener('hashchange', updateContent);
 				updateContent();
 			}
 		};
 	}]);

 	function clearObject(objToClear) {
 		for (var key in objToClear){
		    if (objToClear.hasOwnProperty(key)){
		        delete objToClear[key];
		    }
		}
 	}

 })(angular);
