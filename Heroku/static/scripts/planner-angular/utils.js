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

var momentModule = angular.module('moment', []);
momentModule.service('moment', ['$window', function($window) {
	var moment = $window.moment;
	moment.noConflict = function() {
		delete $window.moment;
		return this;
	};
	moment.range = moment.fn.range;
	return moment//.noConflict();
}]);

momentModule.filter('moment', ['moment', function(moment) {
	return function(input, method /*, args*/) {
		var args = Array.prototype.splice.call(arguments, 2 /* skip input and method */);
		return moment.fn[method].apply(input, args);
	};
}]);

var underscoreModule = angular.module('underscore', []);
underscoreModule.service('_', ['$window', function($window) {
	var _ = $window._;
	var __ = {
		groupBy: _.groupBy
	};
	_.mixin({
		partialRight: function(func) {
			var slice = Array.prototype.slice, args = slice.call(arguments, 1).reverse();
			return function() {
				return func.apply(this, slice.call(arguments, 0).concat(args));
			};
		},
		push: function(arr /*, ... otherArrays*/) {
			var args = Array.prototype.slice.call(arguments, 1); // ignore arr
			args = Array.prototype.concat.apply([], args);
			Array.prototype.push.apply(arr, args);
			return arr;
		},
		ensureArray: function(objOrArr) {
			return _.isArray(objOrArr)? objOrArr : [objOrArr];
		},
		groupBy: function(obj, values, context) {
			if(_.isArray(values)) {
				return _groupByMulti(obj, values, context);
			} else {
				return __.groupBy(obj, values, context);
			}

			function _groupByMulti(obj, values, context) {
				if (values.length === 0)
					return obj;
				var byFirst = _.groupBy(obj, values[0], context),
				    rest = values.slice(1);
				for (var prop in byFirst) {
					byFirst[prop] = _groupByMulti(byFirst[prop], rest, context);
				}
				return byFirst;
			} 
		}
	});
	_.fn = {
		parseInt: function(stuff) {
			return parseInt(stuff, 10);
		},
		/**
		 * Reusable function like in _.pluck to get the property.
		 */
		prop: function(propName) {
			return function(obj) {
				return obj[propName];
			};
		}
	};
	return _//.noConflict();
}]);
