'use strict';
import _ from 'lodash';
var underscoreModule = angular.module('lodash', []);

underscoreModule.service('_', [function() {
		var __ = {
			groupBy: _.groupBy
		};
		_.mixin({
			push: function(arr /*, ... rest*/) {
				var rest = Array.prototype.slice.call(arguments, 1); // ignore arr
				rest = Array.prototype.concat.apply([], rest);
				Array.prototype.push.apply(arr, rest);
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
			},
			isTrue: function(obj) {
				return obj === true;
			},
			isFalse: function(obj) {
				return obj === false;
			}
		});
		return _;
	}
]);
