'use strict';
var module = angular.module('appServices'); // see app.js

module.service('Planner', [
	        '$rootScope', '_', 'moment', 'Cineworld',
	function($rootScope,   _,   moment,   Cineworld) {
		this.defaults = {
			minWaitBetweenMovies: moment.duration({ minutes: 5 }),
			maxWaitBetweenMovies: moment.duration({ minutes: 45 }),
			estimatedAdvertisementLength: moment.duration({ minutes: 15 }),
			endOfWork: moment.duration({ hours: 17, minutes: 30 }),
		};

		this.plan = function (options) {
			const params = _.extend({}, this.defaults, {
				date: Cineworld.date,
				cinemas: Cineworld.selectedCinemas,
				films: Cineworld.selectedFilms,
				performances: Cineworld.performances,
			}, options);
			$rootScope.$broadcast('PlanningStarted', params);
			const plans = plan(params);
			$rootScope.$broadcast('PlanningFinished', plans, params);
			return plans;
		};

		/**
		 * A performance from server.
		 * @typedef {Object} Performance
		 * @property {int} cinema ID
		 * @property {int} film ID
		 * @property {string} date in format YYYYMMDD
		 * @property {Array} performances
		 */
		/**
		 * @param {Object} params
		 * @param {Date} params.date date to plan for; all performances should start on this date
		 * @param {int[]} params.cinemas IDs of cinemas
		 * @param {int[]} params.films IDs of films
		 * @param {Performance[]} params.performances list of performances from server
		 * @param {Moment.Duration} params.minWaitBetweenMovies minimum wait between movies
		 * @param {Moment.Duration} params.maxWaitBetweenMovies maximum wait between movies
		 * @param {Moment.Duration} params.estimatedAdvertisementLength
		 *         estimated length of advertisements (used for schedule planning)
		 * @param {Moment.Duration} params.endOfWork time offset from midnight (within a day)
		 */
		function plan(params) {
			const date = moment(params.date).utc().toISOString();
			const cache = {};
			_.each(params.cinemas, function (cinema) {
				cache[cinema.cineworldID] = {};
				_.each(params.films, function (film) {
					cache[cinema.cineworldID][film.edi] = [];
					const filter = {
						date: date,
						cinema: cinema.cineworldID,
						film: film.edi,
					};
					const filteredPerformances = _.filter(params.performances, filter);
					const performances = _(filteredPerformances).map('performances').flatten(true).value();
					if (performances.length === 0) return;
					cache[cinema.cineworldID][film.edi] = _.map(performances, function (performance) {
						const time = moment.utc(performance.time);
						const plan = _.extend({}, performance, {
							date: () => date,
							cinema: () => cinema,
							film: () => film,
							equals: function (other) {
								return other
								       && this.time === other.time
								       && this.film() === other.film()
								       && this.cinema() === other.cinema()
										;
							},
							scheduledTime: time,
							adLength: params.estimatedAdvertisementLength,
							startTime: time.clone()
									.add(params.estimatedAdvertisementLength),
							endTime: time.clone()
									.add(params.estimatedAdvertisementLength)
									.add(film.runtime, 'minutes'),
						});
						plan.range = moment.range(plan.startTime, plan.endTime);
						plan.fullRange = moment.range(plan.scheduledTime, plan.endTime);
						return plan;
					});
				});
			});
			//noinspection UnnecessaryLocalVariableJS useful for debugging
			const results = _.map(params.cinemas, function (cinema) {
				const cinemaResults = {
					cinema: cinema,
					valid: [],
					offending: [],
				};
				const rootNode = {
					watched: ["travel"],
					performance: {
						endTime: moment.utc(params.date, 'YYYYMMDDHH'),
						film: {
							edi: -1,
						},
						range: moment.range(),
					},
					next: [],
					level: 0,
				};
				planCinema(cinemaResults, cache, cinema, params.films, [rootNode]);
				return cinemaResults;
			});

			return results;

			function planCinema(results, cache, cinema, films, graph) {
				_.each(graph, function (node) {
					// calculate possible next movies
					_.each(films, function (film) {
						if (!_.includes(node.watched, film.edi)) { // didn't watch it already
							const performances = cache[cinema.cineworldID][film.edi];
							_.each(performances, function (perf) {
								if (node.performance.endTime < perf.startTime) { // starts after the other finishes
									node.next.push({
										watched: node.watched.concat([perf.film().edi]),
										performance: perf,
										next: [],
										prev: node,
										level: node.level + 1,
									});
								}
							});
						}
					});
					if (node.next.length !== 0) {
						// recurse, try to watch next movies
						planCinema(results, cache, cinema, films, node.next);
					} else // we're on a graph leaf, process results 
					if (node.watched.length < 2 || (node.watched.length === 2 && films.length > 1)) {
						// don't care about these:
						// node.watched.length === 0 => errors?
						// node.watched.length === 1 => "travel" only, no film
						// node.watched.length === 2 => "travel" + a film
						//                              (but only last occurrence if films.length > 1)
					} else {
						// node.watched.length > 2   => "travel" + a plan (multiple films) 
						const plan = unfold(node);
						plan.first = plan[0];
						plan.last = plan[plan.length - 1];
						plan.range = moment.range(plan.first.range.start, plan.last.range.end);
						_.reduce(plan, function (prev, current) {
							const breakLength = moment.duration(current.range.start - prev.range.end);
							current.breakBefore = breakLength;
							prev.breakAfter = breakLength;
							return current;
						});

						plan.endOfWork = plan.range.start.clone().local().startOf('day').add(params.endOfWork);
						_.each(plan, function (performance) {
							performance.offenses = {
								shortBreak: performance.breakBefore < params.minWaitBetweenMovies,
								longBreak: performance.breakBefore > params.maxWaitBetweenMovies,
								earlyStart: performance.range.start.isBefore(plan.endOfWork),
								earlyFinish: performance.range.end.isBefore(plan.endOfWork),
							};
						});
						plan.offenses = {
							fewMovies: !(films.length + 1 === node.watched.length && node.watched.length !== 1),
							earlyStart: plan.range.start.isBefore(plan.endOfWork),
							earlyFinish: plan.range.end.isBefore(plan.endOfWork),
							shortBreak: _.some(plan, 'offenses.shortBreak'),
							longBreak: _.some(plan, 'offenses.longBreak'),
						};

						const increaseIfOffending = (offenseCount, offense) => offenseCount + (offense ? 1 : 0);
						plan.offenses.count = _.reduce(plan.offenses, increaseIfOffending, 0);
						if (plan.offenses.count === 0) {
							results.valid.push(plan);
						} else {
							results.offending.push(plan);
						}
					}

					function unfold(node) {
						const unfolded = [];
						while (node.prev) {
							unfolded.unshift(_.clone(node.performance));
							node = node.prev;
						}
						return unfolded;
					}
				});
			}
		}
	},
]);

