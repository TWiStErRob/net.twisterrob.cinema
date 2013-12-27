'use strict';
var module = angular.module('appServices'); // see app.js

module.service('Planner', [
	        '$rootScope', '_', 'Cineworld',
	function($rootScope,   _,   Cineworld) {
		var config = {
			defaults: {

			}
		};

		this.plan = function(options) {
			var params = _.extend({}, config.defaults, {
				date: Cineworld.date,
				cinemas: Cineworld.selectedCinemaIDs,
				films: Cineworld.selectedFilmEDIs,
				performances: Cineworld.performances
			}, options);
			$rootScope.$broadcast('PlanningStarted', params);
			var plans = plan(params);
			$rootScope.$broadcast('PlanningFinished', plans, params);
			return plans;
		};

		/**
		 * @param params {
		 * 	date: new Date(),
		 * 	cinemas: [1,2,3, ...],
		 *  films: [12345, 12346, ...],
		 * 	performances: [ { cinema: 1, film: 12345, date: "20131230", performances: [] }, ...]
		 * }
		 */
		function plan(params) {
			var results = ["test"];
			return results;
		}
	}
]);
