'use strict';
import Moment from 'moment';
import { extendMoment } from 'moment-range';
const moment = extendMoment(Moment);

var momentModule = angular.module('moment', []);

momentModule.service('moment', [
	        '$window',
	function($window) {
		//moment.relativeTimeRounding(value => Math.round(value * 100) / 100);
		//TODO localize this once https://github.com/moment/moment/issues/4295 lands
		moment.relativeTimeThreshold('s', 60);
		moment.relativeTimeThreshold('ss', 0); // must be after 's', disables "few seconds"
		moment.relativeTimeThreshold('m', 60);
		moment.relativeTimeThreshold('h', 24);
		moment.relativeTimeThreshold('d', 31);
		moment.relativeTimeThreshold('M', 12);
		/**
		 * Return a precize human readable string representing the given moment duration.
		 *
		 * @param {Moment.Duration} duration
		 * @param {{mostPreciseUnit: string, numberOfSignificantParts: integer}} options
		 * @see https://github.com/moment/moment/issues/348#issuecomment-348944860
		 */
		moment.duration.fn.humanizePrecisely = function(options = {}) {
			// Split the duration into parts to be able to filter out unwanted ones
			const allParts = [
				{ value: this.years(), unit: 'years' },
				{ value: this.months(), unit: 'months' },
				{ value: this.days(), unit: 'days' },
				{ value: this.hours(), unit: 'hours' },
				{ value: this.minutes(), unit: 'minutes' },
				{ value: this.seconds(), unit: 'seconds' },
				// cannot format with moment.humanize()
				//{ value: duration.milliseconds(), unit: 'milliseconds' },
			];

			return _(allParts)
				// only use the first parts until the most precise unit wanted
				.take(_.findIndex(allParts, {unit: options.mostPreciseUnit || 'seconds'}) + 1)
				// drop the most significant parts with a value of 0
				.dropWhile((part) => part.value === 0)
				// skip other zeroes in the middle (moment.humanize() can't format them)
				.reject((part) => part.value === 0)
				// use only the significant parts requested
				.take(options.numberOfSignificantParts || allParts.length)
				// format each part
				.map((part) => moment.duration(part.value, part.unit).locale(this.locale()).humanize())
				.join(' ');
		}
		return moment;
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

momentModule.filter('humanizeDuration', [
	        'moment',
	function(moment) {
		return function(input/*, args*/) {
			if (!moment.isDuration(input)) {
				throw new Error(input + " is not a moment duration");
			}
			return input.humanizePrecisely();
		};
	}
]);
