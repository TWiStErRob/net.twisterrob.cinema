'use strict';
var module = angular.module('appServices'); // see app.js

module.service('Planner', [
	        '$rootScope', '_', 'Cineworld',
	function($rootScope,   _,   Cineworld) {
		var config = {
			defaults: {
				minWaitBetweenMovies: 5 /* minutes */,
				maxWaitBetweenMovies: 45 /* minutes */,
				estimatedAdvertisementLength: 15 /* minutes */,

			}
		};

		this.plan = function(options) {
			var params = _.extend({}, config.defaults, {
				date: Cineworld.date,
				cinemas: Cineworld.selectedCinemas,
				films: Cineworld.selectedFilms,
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
			var date = moment(params.date);
			var cache = {};
			_.each(params.cinemas, function(cinema) {
				cache[cinema.cineworldID] = {};
				_.each(params.films, function(film) {
					cache[cinema.cineworldID][film.edi] = [];
					var filter = {
						date: date.format('YYYYMMDD'),
						cinema: cinema.cineworldID,
						film: film.edi
					};
					var performances = _.where(params.performances, filter);
					performances = _.flatten(_.pluck(performances, 'performances'), true);
					if(performances.length === 0) return;
					cache[cinema.cineworldID][film.edi] = _.map(performances, function(performance) {
						var time = moment(filter.date + performance.time, 'YYYYMMDDHH:mm');
						var plan = _.extend({}, performance, {
							date: date,
							cinema: cinema,
							film: film,
							scheduledTime: time,
							adLength: params.estimatedAdvertisementLength,
							startTime: time.clone()
							               .add('minutes', params.estimatedAdvertisementLength),
							endTime: time.clone()
							             .add('minutes', params.estimatedAdvertisementLength)
							             .add('minutes', film.runtime),
						});
						plan.range = moment().range(plan.startTime, plan.endTime);
						plan.fullRange = moment().range(plan.scheduledTime, plan.endTime);
						return plan;
					});
				});
			});
			var results = _.indexBy(_.map(params.cinemas, function(cinema) {
				var cinemaResults = {
					cinema: cinema,
					all: [],
					shortBreak: [],
					longBreak: [],
					fewMovies: [],
					early: [],
					multiple: []
				};
				planCinema(cinemaResults, cache, cinema, params.films, [{
					watched: ["travel"],
					performance: {
						endTime: moment(params.date, 'YYYYMMDDHH'),
						film: {
							edi: -1
						},
						range: {
							end: moment()
						}
					},
					next: [],
					level: 0
				}]);
				return cinemaResults;
			}), function(result) {
				return result.cinema.cineworldID;
			});

			return results;

			function planCinema(results, cache, cinema, films, graph) {
				_.each(graph, function(node) {
					// calculate possible next movies
					_.each(films, function(film) {
						if(!_.contains(node.watched, film.edi)) { // didn't watch it already
							var performances = cache[cinema.cineworldID][film.edi];
							_.each(performances, function(perf) {
								if(node.performance.endTime < (perf.startTime)) { // starts after the other finishes
									node.next.push({
										watched: node.watched.concat([perf.film.edi]),
										performance: perf,
										next: [],
										prev: node,
										level: node.level + 1
									});
								}
							});
						}
					});
					if(node.next.length != 0) {
						// recurse, try to watch next movies
						planCinema(results, cache, cinema, films, node.next);
					} else if (node.watched.length > 2) { // we're on a graph leaf, process results
						var plan = unfold(node);
						plan.first = plan[0];
						plan.last = plan[plan.length - 1];
						plan.range = moment().range(plan.first.range.start, plan.last.range.end);
						_.reduce(plan, function(prev, current) {
							current.breakLength = Math.floor((current.range.start - prev.range.end) / 60 / 1000);
							return current;
						});

						var workEnd = plan.range.start.clone().startOf('day').add(moment.duration({hours:17, minutes:30}));
						plan.offenses = {
							fewMovies: !(films.length + 1 == node.watched.length && node.watched.length != 1),
							early: plan.range.start.isBefore(workEnd),
							tooShort: !!_.find(plan, function(unfold) {
								return unfold.breakLength < params.minWaitBetweenMovies;
							}),
							tooLong: !!_.find(plan, function(unfold) {
								return unfold.breakLength > params.maxWaitBetweenMovies;
							})
						};

						plan.offenses.count = _.reduce(plan.offenses, function(offenseCount, offense) { return offenseCount + (offense ? 1 : 0); }, 0);
						if (plan.offenses.count === 0) {
							results.all.push(plan);
						} else if (plan.offenses.count > 1) {
							results.multiple.push(plan);
						} else {
							if(early) {
								results.early.push(plan);
							}
							if(tooShort) {
								results.shortBreak.push(plan);
							}
							if(tooLong) {
								results.longBreak.push(plan);
							}
							if(fewMovies) {
								results.fewMovies.push(plan);
							}
						}
					} else {
						// node.watched.length == 0 | 1 ==> don't care about these (last single movies (1), and errors(0?) )
					}
					function unfold(node) {
						var unfolded = [];
						while(node.prev) {
							unfolded.unshift(_.clone(node.performance));
							node = node.prev;
						}
						return unfolded;
					}
				});
			}
		}
	}
]);
