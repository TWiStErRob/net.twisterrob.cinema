'use strict';
import Moment from 'moment';
import { extendMoment } from 'moment-range';
const moment = extendMoment(Moment);

var momentModule = angular.module('moment', []);

momentModule.service('moment', [
	        '$window',
	function($window) {
		moment.noConflict = function() {
			delete $window.moment;
			return this;
		};
		moment.range = moment.fn.range;
		return moment;//.noConflict();
	}
]);

momentModule.filter('moment', [
	        'moment',
	function(moment) {
		return function(input, method /*, args*/) {
			var args = Array.prototype.splice.call(arguments, 2 /* skip input and method */);
			return moment.fn[method].apply(moment(input), args);
		};
	}
]);

momentModule.filter('momentLocalFormat', [
	        'moment',
	function(moment) {
		return function(input/*, args*/) {
			var args = Array.prototype.splice.call(arguments, 1 /* skip input */);
			return moment.fn.format.apply(moment(input).local(), args);
		};
	}
]);
