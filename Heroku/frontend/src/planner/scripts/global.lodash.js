'use strict';
import _ from 'lodash';
var underscoreModule = angular.module('lodash', []);

underscoreModule.service('_', [function() {
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
	}
]);
