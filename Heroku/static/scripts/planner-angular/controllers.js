'use strict';
var module = angular.module('appControllers'); // see app.js

module.controller('AppController', [
	        '$rootScope', '_', 'moment', 'Cineworld', '$location',
	function($scope,       _,   moment,   cineworld,   $location) {
		var search = {
			film: 'f',
			cinema:'c',
			date: 'd',
			dateFormat: 'YYYY-MM-DD'
		};
		$scope.cineworld = cineworld;

		// Two-way binding for cinemas
		$scope.$watch(function() {
			return $location.search()[search.cinema];
		}, function(cinemaIDs) {
			cinemaIDs = _.ensureArray(cinemaIDs);
			cinemaIDs = _.map(cinemaIDs, _.fn.parseInt);
			_.push($scope.cineworld.pendingSelectedCinemas, cinemaIDs);
		});
		$scope.$watch('cineworld.cinemas | filter: { selected: true }', function (newValue, oldValue, scope) {
			$location.search(search.cinema, _.pluck(newValue, 'cineworldID')).replace();
		}, true);

		// Two-way binding for films
		$scope.$watch(function() {
			return $location.search()[search.film];
		}, function(filmEDIs) {
			filmEDIs = _.ensureArray(filmEDIs);
			filmEDIs = _.map(filmEDIs, _.fn.parseInt);
			_.push($scope.cineworld.pendingSelectedFilms, filmEDIs);
		});
		$scope.$watch('cineworld.films | filter: { selected: true }', function (newValue, oldValue, scope) {
			$location.search(search.film, _.pluck(newValue, 'edi')).replace();
		}, true);

		// Two-way binding for date, order of watches are important, first takes precedence on load
		$scope.$watch(function() {
			return $location.search()[search.date];
		}, function(date) {
			var m = moment(date, search.dateFormat);
			if(m.isValid()) {
				$scope.cineworld.date = m.toDate();
			}
		});
		$scope.$watch('cineworld.date', function (newValue, oldValue, scope) {
			$location.search(search.date, moment(newValue).format(search.dateFormat)).replace();
		}, true);
	}
]);

module.controller('StatusController', [
	        '$scope', 'Status',
	function($scope,   Status) {
		$scope.stati = Status.stati;
		$scope.$on('ResourceError', function(event, error) {
			var message = error.config.method + ' ' + error.config.url;
			message += ': ' + error.status + '/' + error.data;
			Status.showStatus(message);
		});
		$scope.$on('CinemasLoaded', function(event, cinemas) {
			Status.showStatus('Loaded ' + cinemas.length + ' cinemas.');
		});
		$scope.$on('FilmsLoaded', function(event, films) {
			Status.showStatus('Loaded ' + films.length + ' films.');
		});
		$scope.$on('CinemaFavorited', function(event, cinema) {
			Status.showStatus('Cinema favorited: ' + cinema.name);
		});
		$scope.$on('CinemaUnFavorited', function(event, cinema) {
			Status.showStatus('Cinema un-favorited: ' + cinema.name);
		});
	}
]);

module.controller('DebugController', [
	        '$scope', 'moment', '_', '$compile',
	function($scope,   moment,   _,   $compile) {
	}
]);

module.controller('DateController', [
	        '$scope', '$timeout',
	function($scope,   $timeout) {
		$scope.$watch('cineworld.date', function (newValue, oldValue, scope) {
			$scope.cineworld.updateFilms();
		}, true);

		$scope.cineworldDatePickerDisplayed = false;
		$scope.displayCineworldDatePicker = function() {
			$timeout(function() {
				$scope.cineworldDatePickerDisplayed = true;
			});
		};
	}
]);

module.controller('CinemasController', [
	        '$rootScope', '$scope', '_', 'Cinema',
	function($rootScope,   $scope,   _,   Cinema) {
		$scope.loading = true;
		$scope.$on('CinemasLoading', function(event) {
			$scope.loading = true;
		});
		$scope.$on('CinemasLoaded', function(event, cinemas) {
			if(!_.any(cinemas, _.fn.prop('selected'))) {
				$scope.buttonClick($scope.buttons.london);
			}
			$scope.loading = false;
		});
		$scope.cineworld.updateCinemas();

		$scope.buttons = {
			all: { label: "All", handle: function(cinema) {
				cinema.selected = true;
			} },
			none: { label: "None", handle: function(cinema) {
				cinema.selected = false;
			} },
			invert: { label: "Invert", handle: function(cinema) {
				cinema.selected = !cinema.selected;
			}, hidden: true },
			london: { label: "London", handle: function(cinema) {
				cinema.selected = /London/.test(cinema.name);
			} },
			favs: { label: "Favorites", handle: function(cinema) {
				cinema.selected = !!cinema.fav;
			} }
		};
		$scope.buttonClick = function(button) {
			angular.forEach($scope.cineworld.cinemas, button.handle);
		};

		$scope.favClick = function(cinema) {
			if(cinema.favLoading) return;
			cinema.favLoading = true;
			var params = {cinemaID: cinema.cineworldID};
			if(cinema.fav) {
				Cinema.unFav(params).$promise.then(
					function(/*ignore*/ newCinema) {
						cinema.fav = false;
						cinema.favLoading = false;
						$rootScope.$broadcast('CinemaUnFavorited', cinema);
					},
					function(error) {
						$rootScope.$broadcast('ResourceError', error);
					}
				);
			} else {
				Cinema.fav(params).$promise.then(
					function(/*ignore*/ newCinema) {
						cinema.fav = true;
						cinema.favLoading = false;
						$rootScope.$broadcast('CinemaFavorited', cinema);
					},
					function(error) {
						$rootScope.$broadcast('ResourceError', error);
					}
				);
			}
		};
	}
]);

module.controller('ViewPopupController', function($scope, $timeout, $modalInstance, cinemas, cinema, film, date) {
	$scope.cinemas = cinemas;
	$scope.selected = {
		cinema: cinema,
		film: film,
		date: date,
		time: date,
		friends: []
	};

	$scope.cinemaGroup = function(cinema) {
		return cinema.fav ? "Favorites" : "Others";
	};

	$scope.uiState = {
		datePickerDisplayed: false,
		displayDatePicker: function() {
			$timeout(function() {
				// TODO figure out why doesn't it work it's supposed to (location of function doesn't matter):
				// 1. doesn't work without $digest
				// 2. doesn't work without uiState intermediate object (not even with $digest)
				$scope.uiState.datePickerDisplayed = true;
				$scope.$digest();
			});
		}
	};

	$scope.ok = function () {
		$modalInstance.close($scope.selected);
	};
	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};
});

module.controller('FilmsController', [
	        '$scope', '$modal', 'Film',
	function($scope,   $modal,   Film) {
		$scope.$watch('cineworld.cinemas | filter: { selected: true }', function (newValue, oldValue, scope) {
			$scope.cineworld.updateFilms();
		}, true);
		$scope.loading = true;
		$scope.$on('FilmsLoading', function(event) {
			$scope.loading = true;
		});
		$scope.$on('FilmsLoaded', function(event, films) {
			$scope.loading = false;
		});

		$scope.viewPopup = function(film) {
			if(film.view || film.addingView) return;
			var modalInstance = $modal.open({
				templateUrl: 'viewPopup.shtml',
				controller: 'ViewPopupController',
				resolve: {
					cinemas: function () {
						return $scope.cineworld.cinemas;
					},
					cinema: function() {
						return _($scope.cineworld.cinemas).sortBy('name').find('selected');
					},
					film: function() {
						return film;
					},
					date: function() {
						return moment().startOf('day').add('hours', 18).toDate();
					}
				}
			});

			modalInstance.result.then(
				function (modalResult) {
					if(film !== modalResult.film) throw "Must be the same";
					film.addingView = true;
					Film.addView({
						edi: film.edi,
						cinema: modalResult.cinema.cineworldID
					}, function(view) {
						film.view = view;
						film.addingView = false;
					});
					$scope.selected = modalResult;
				},
				function () {
					// ignore cancel
				}
			);
		};
	}
]);

module.controller('PerformancesController', [
	        '$scope', '_', 'Planner',
	function($scope,   _,   Planner) {
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
			var plans = Planner.plan();
			_.each(plans, function(plan) {
				plan.display = plan.valid.length > 0;
			});
			$scope.plans = plans;
		});

		$scope.offenseCount = function(plan) {
			return plan.offenses.count;
		};
		$scope.offensePriority = function(plan) {
			return _(plan.offenses)
					.pick(function(offense) { return offense === true; })
					.keys()
					.reduce(aggregateOffensePriority, 0);

			function aggregateOffensePriority(sum, offense) {
				var prio = ['fewMovies', 'shortBreak', 'longBreak', 'early']; // lower is better
				// use shifting to produce a non-conflicting result (fewMovies + shortBreak != longBreak)
				// also ECMA-262§11.7.1.7-8: (2 << -1) === (2 << 0xFFFFFFFFFF & 0x1F) === (2 << 31) === (0b10 << 31) === 0b10...0 [32 zeros],
				// which is 1 bit bigger than 32 bit, so it's truncated as 0 === (2 << -1)
				return sum + (2 << _.indexOf(prio, offense));
			}
		};

		$scope.cleanName = function(cinemaName) {
			return cinemaName.replace("London - ", "");
		};
	}
]);
