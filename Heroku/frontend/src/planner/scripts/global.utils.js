'use strict';
var module = angular.module('appUtils'); // see app.js

module.service('AngularHacks', ['$window', function($window) {
	this.fixNextJSONP = function () {
		var c = $window.angular.callbacks.counter;
		$window['angularcallbacks_' + c] = function (data) {
			$window.angular.callbacks['_' + c](data);
			delete $window['angularcallbacks_' + c];
		};
	};
}]);
