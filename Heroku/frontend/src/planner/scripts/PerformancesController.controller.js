'use strict';
var module = angular.module('appControllers'); // see app.js

module.controller('PerformancesController', [
	        '$scope', '_', '$uibModal', 'moment', 'Planner', 'orderByFilter',
	function($scope,   _,   $uibModal,   moment,   Planner,   orderByFilter) {
		$scope.$watch('cineworld.films | filter: { selected: true }', function (newValue, oldValue, scope) {
			$scope.cineworld.updatePerformances();
		}, true);
		$scope.loading = true;
		$scope.$on('PerformancesLoading', function(event) {
			$scope.loading = true;
		});
		$scope.$on('PerformancesLoaded', function(event, performances) {
			var x = _.groupBy(performances, ['cinema', 'film']);
			_.each(x, function(y, xk) {
				_.each(y, function(z, yk) {
					if(z.length !== 1) console.error("Cinema " + xk + ", film " + yk + " has multiple objects: " + z.length, z);
					y[yk] = z[0].performances;
				});
			});
			$scope.performances = x;
			$scope.loading = false;
			$scope.plan();
		});

		$scope.buttons = {
			plan: { label: "Plan", handle: function() {
				$scope.plan();
			} },
			options: { label: "Options", handle: function() {
				$scope.optionsPopup();
			} }
		};
		$scope.buttonClick = function(button) {
			return button.handle();
		};

		$scope.plan = function() {
			var plans = Planner.plan($scope.options);
			_.each(plans, function(plan) {
				function More(additional) {
					this.list = [];
					this._extra = additional;
				}
				More.prototype.reset = function() {
					var all = this.list.concat(this._extra);
					this._extra = orderByFilter(all, [$scope.offensePriority]);
					this.list = [];
					delete this.filteredByFilm;
					delete this.filteredByScreening;
				};
				More.prototype.splitBy = function(matcher) {
					this.reset();
					this.list = _.filter(this._extra, matcher);
					this._extra = _.reject(this._extra, matcher);
				};
				More.prototype.initialState = function() {
					this.splitBy(function(plan) {
						return plan.offenses.count === 0
						       || (plan.offenses.count === 1 && plan.offenses.fewMovies);
					});
					if (plan.scheduleExplorer) {
						this.showMore(15 - plan.more.list.length);
					}
				};
				More.prototype.showMore = function(count) {
					count = Math.max(0, count);
					count = Math.min(count, this._extra.length);
					var more = this._extra.splice(0, count);
					this.list.push.apply(this.list, more);
					delete this.filteredByFilm;
					delete this.filteredByScreening;
				};
				More.prototype.remaining = function() {
					return this._extra.length;
				};
				More.prototype.isFiltering = function() {
					return this.filteredByFilm || this.filteredByScreening;
				};
				More.prototype.filterFilm = function (film) {
					this.splitBy(function (plan) {
						return _.some(plan, function (screening) {
							return screening.film() === film;
						});
					});
					this.filteredByFilm = film;
				};
				More.prototype.filterScreening = function (movie) {
					this.splitBy(function (plan) {
						return _.some(plan, function (screening) {
							return screening.equals(movie);
						});
					});
					this.filteredByScreening = movie;
				};

				plan.more = new More(plan.valid.concat(plan.offending));
				plan.scheduleExplorer = false;
				plan.more.initialState();
				plan.open = plan.more.list.length > 0;
			});
			$scope.plans = plans;
		};

		$scope.focus = function(cinema) {
			_.each($scope.plans, function(plan) {
				plan.open = plan.cinema === cinema;
			});
		};
		$scope.filterFilm = function(film) {
			_.each($scope.plans, function(plan) {
				plan.more.filterFilm(film);
				plan.open = plan.more.list.length > 0;
			});
		};
		$scope.filterScreening = function(cinema, film, performance) {
			var movie = { // to satisfy equals() contract
				cinema: function() { return cinema; },
				film: function() { return film; },
				time: performance.time
			};
			_.each($scope.plans, function(plan) {
				plan.more.filterScreening(movie);
				plan.open = plan.more.list.length > 0;
			});
		};

		$scope.options = _.cloneDeep(Planner.defaults);
		$scope.optionsPopup = function() {
			var modalInstance = $uibModal.open({
				templateUrl: 'templates/planOptionsPopup.html',
				controller: 'PlanOptionsPopupController',
				resolve: {
					options: function () {
						return _.cloneDeep($scope.options);
					}
				}
			});

			modalInstance.result.then(
				function (modalResult) {
					$scope.options = modalResult;
					$scope.plan();
				},
				function () {
					// ignore cancel
				}
			);
		};

		/**
		 * 1. Plans with valid plans
		 * 2. Plans with offending suggestions
		 * 3. Plans without anything
		 */
		$scope.planRank = function(plan) {
			return plan.valid.length? 1 : (plan.offending.length? 2 : 3);
		};
		$scope.offenseCount = function(plan) {
			return plan.offenses.count;
		};
		$scope.offensePriority = function(plan) {
			return _(plan.offenses)
					.pickBy(_.isTrue)
					.keys()
					.reduce(aggregateOffensePriority, 0);

			function aggregateOffensePriority(sum, offense) {
				var prio = ['fewMovies', 'shortBreak', 'longBreak', 'earlyStart', 'earlyFinish', 'dealBreak']; // lower is better
				// Given var prio = ['a', 'b', 'c', 'd']:
				// if I used the index of each element conflicts would arise: b(1) + c(2) != d(3).
				// So use shifting to produce a bitmap and add them to create a non-conflicting result.
				// Also ECMA-262§11.7.1.7-8: (2 << -1) === (2 << 0xFFFFFFFFFF & 0x1F) === (2 << 31) === (0b10 << 31) === 0b10...0 [32 zeros],
				// which is 1 bit bigger than 32 bit, so it's truncated as 0 === (2 << -1), hence it's safe to use _.indexOf without any checks.
				return sum + (2 << _.indexOf(prio, offense));
			}
		};
		$scope.offenseDisplay = function(offender, offenses) {
			const offending = _.filter(offenses, (offense) => offender.offenses[offense]);
			if (_.includes(offending, 'earlyFinish') && _.includes(offending, 'earlyStart')) {
				return 'starts and ends too early';
			} else if (_.includes(offending, 'earlyFinish')) {
				return 'ends too early';
			} else if (_.includes(offending, 'earlyStart')) {
				return 'starts too early';
			}
		};

		const visOffset = moment.duration(6, 'hours');
		$scope.fullDay = moment.duration(1, 'day');
		/**
		 * @param {duration|int} part
		 * @param {duration|int} full
		 * @returns {string} percentage
		 */
		$scope.widthOf = function (part, full) {
			return `${moment.duration(part) / moment.duration(full) * 100}%`;
		};
		/**
		 * @param {DateRange} range
		 * @returns {string} percentage
		 */
		$scope.widthOfBefore = function (range) {
			const time = range.start.clone().local();
			const startOfDay = time.clone().startOf('day').add(visOffset);
			const sinceStartOfDay = moment.duration(time - startOfDay);
			return $scope.widthOf(sinceStartOfDay, $scope.fullDay);
		};
		/**
		 * @param {DateRange} range
		 * @returns {string} percentage
		 */
		$scope.widthOfAfter = function (range) {
			const time = range.end.clone().local();
			const endOfDay = range.start.clone().local().endOf('day').add(visOffset);
			const tillEndOfDay = moment.duration(endOfDay - time);
			return $scope.widthOf(tillEndOfDay, $scope.fullDay);
		};

		$scope.ticks = function () {
			const ticks = [];
			console.assert(visOffset.asHours() === Math.floor(visOffset.asHours()), visOffset);
			// (visOffset, visOffset + 24) open-interval, so there are no markers near edges
			for (var i = visOffset.asHours() + 1; i < visOffset.asHours() + 24; i += 1) {
				if (i % 24 === 0) { // end of day mark
					ticks.push({ time: i * 60, width: 5, color: '#ffffff' });
				} else if (i % 6 === 0) { // quarter days
					ticks.push({ time: i * 60, width: 5, color: '#666666' });
				} else { // normal hours
					ticks.push({ time: i * 60, width: 2, color: '#222222' });
				}
			}
			// special times time
			ticks.push({ time: $scope.options.endOfWork.asMinutes(), width: 5, color: '#487a7a' });

			const steps = _(ticks)
					.sortBy(['time', 'width'])
					.map(tick => {
						tick.time -= visOffset.asMinutes();
						return tick;
					})
					.flatMap(function (tick) {
						const toPercentOfDay = 1 / (24 * 60) * 100;
						const time = tick.time * toPercentOfDay;
						const girth = tick.width * toPercentOfDay;
						return [
							`transparent ${time - girth}%`,
							`${tick.color} ${time - girth}%`,
							`${tick.color} ${time + girth}%`,
							`transparent ${time + girth}%`,
						];
					})
					.value();
			return `linear-gradient(
				to right,
				transparent 0%,
				${steps.join(',')},
				transparent 100%
			)`;
		};
	}
]);

module.run(['$templateCache', function ($templateCache) {
	$templateCache.put('templates/planOptionsPopup.html', require('../templates/planOptionsPopup.html'));
}]);
