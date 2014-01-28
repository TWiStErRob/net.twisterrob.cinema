'use strict';
var momentModule = angular.module('moment', []);

momentModule.service('moment', [
	        '$window',
	function($window) {
		var moment = $window.moment;
		moment.noConflict = function() {
			delete $window.moment;
			return this;
		};
		moment.range = moment.fn.range;
		return moment//.noConflict();
	}
]);

momentModule.filter('moment', [
	        'moment',
	function(moment) {
		return function(input, method /*, args*/) {
			var args = Array.prototype.splice.call(arguments, 2 /* skip input and method */);
			return moment.fn[method].apply(input, args);
		};
	}
]);
