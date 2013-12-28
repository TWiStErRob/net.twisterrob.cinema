'use strict';
var module = angular.module('appFilters'); // see app.js

module.filter('moment', ['$window', function($window) {
	return function(input, method /*, args*/) {
		var args = Array.prototype.splice.call(arguments, 2 /* skip input and method */);
		return $window.moment.fn[method].apply(input, args);
	};
}]);
