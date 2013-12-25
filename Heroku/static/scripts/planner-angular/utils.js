'use strict';
var module = angular.module('appUtils'); // see app.js

module.service('AngularHacks', function _constructor() { 
	this.fixNextJSONP = function () { 
		var c = $window.angular.callbacks.counter;
		$window['angularcallbacks_' + c] = function (data) {
			$window.angular.callbacks['_' + c](data);
			delete $window['angularcallbacks_' + c];
		};
	};
});

var underscore = angular.module('underscore', []);
underscore.service('_', function() {
	var _ = window._.noConflict();
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
	return _; // assumes underscore has already been loaded on the page
});
