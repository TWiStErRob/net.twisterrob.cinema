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
			all: { label: "All", selector: function(cinema) {
				cinema.selected = true;
			} },
			none: { label: "None", selector: function(cinema) {
				cinema.selected = false;
			} },
			invert: { label: "Invert", selector: function(cinema) {
				cinema.selected = !cinema.selected;
			}, hidden: true },
			london: { label: "London", selector: function(cinema) {
				cinema.selected = /London/.test(cinema.name);
			} },
			favs: { label: "Favorites", selector: function(cinema) {
				cinema.selected = !!cinema.fav;
			} }
		};
		$scope.buttonClick = function(button) {
			if(button.handle) {
				return button.handle();
			} else {
				_.forEach($scope.cineworld.cinemas, button.selector);
			}
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

module.controller('ViewPopupController', function($scope, $timeout, $modalInstance, cinemas, defaultCinema, films, defaultFilm, defaultDate) {
	$scope.cinemas = cinemas;
	$scope.films = films;
	$scope.selected = {
		cinema: defaultCinema,
		film: defaultFilm,
		date: defaultDate,
		friends: []
	};

	$scope.cinemaGroup = function(cinema) {
		return cinema.fav ? "Favorites" : "Others";
	};
	$scope.filmGroup = function(film) {
		return film.view ? "Watched" : "New";
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
	        '$scope', '$modal', '$dialog', '_', 'moment', 'Film',
	function($scope,   $modal,   $dialog,   _,   moment,   Film) {
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

		$scope.buttons = {
			all: { label: "All", selector: function(film) {
				film.selected = true;
			} },
			none: { label: "None", selector: function(film) {
				film.selected = false;
			} },
			invert: { label: "Invert", selector: function(film) {
				film.selected = !film.selected;
			}, hidden: true },
			new: { label: "New", selector: function(film) {
				film.selected = !film.view; // doesn't have a View
			} },
			watched: { label: "Watched", selector: function(film) {
				film.selected = !!film.view; // has view
			}, hidden: true },
			addView: { label: "Add View", handle: function() {
				$scope.addViewPopup(null, null);
			} }
		};
		$scope.buttonClick = function(button) {
			if(button.handle) {
				return button.handle();
			} else {
				_.forEach($scope.cineworld.films, button.selector);
			}
		};

		function addView(film, cinema, date) {
			if(film.processingView) return;
			film.processingView = true;
			Film.addView({
				edi: film.edi,
				cinema: cinema.cineworldID,
				date: moment(date).utc().valueOf()
			}, function(view) {
				var existing = _($scope.cineworld.films).find(function(film) {
					return film.view
						&& film.view.cinema.cineworldID === view.cinema.cineworldID
						&& film.view.film.cineworldID === view.film.cineworldID
						&& film.view.date === view.date
					;
				});
				if(existing) {
					existing.view = view;
				} else if (_($scope.cineworld.films).filter({ edi: view.film.edi, view: null }).size() === 1) {
					film.view = view;
				} else {
					existing = _.clone(view.film);
					existing.view = view;
					$scope.cineworld.films.push(existing);
				}
				film.processingView = false;
			});
		}
		$scope.addViewPopup = function(cinema, film) {
			var modalInstance = $modal.open({
				templateUrl: 'viewPopup.shtml',
				controller: 'ViewPopupController',
				resolve: {
					cinemas: function () {
						return $scope.cineworld.cinemas;
					},
					films: function() {
						return $scope.cineworld.films;
					},
					defaultCinema: function() {
						return cinema || _($scope.cineworld.cinemas).sortBy('name').find('selected');
					},
					defaultFilm: function() {
						return film || _($scope.cineworld.films).sortBy('title').find('selected');
					},
					defaultDate: function() {
						return moment().startOf('day').add('hours', 18).toDate(); // today at 6 pm
					}
				}
			});

			modalInstance.result.then(
				function (modalResult) {
					addView(modalResult.film, modalResult.cinema, modalResult.date);
				},
				function () {
					// ignore cancel
				}
			);
		};

		function removeView(view) {
			var films = _($scope.cineworld.films).filter({ edi: view.film.edi });
			if(films.any('processingView')) return;
			films.each(function(film) { film.processingView = true; });
			Film.removeView({
				edi: view.film.edi,
				cinema: view.cinema.cineworldID,
				date: view.date // not conversion, it's already UTC
			}, function() {
				if(films.size() === 1) {
					films.first().view = null;
				} else {
					var film = _($scope.cineworld.films).find(function(film) {
						return film.view
							&& film.view.cinema.cineworldID === view.cinema.cineworldID
							&& film.view.film.cineworldID === view.film.cineworldID
							&& film.view.date === view.date
						;
					});
					if(film) {
						_.pull($scope.cineworld.films, film);
					} else {
						console.error("Should have found it.", view);
					}
				}
				films.each(function(film) { film.processingView = false; });
			});
		}
		$scope.removeViewPopup = function(film) {
			$dialog
				.prompt("Deleting a View", "Are you sure you want to delete this view of " + film.title + "?")
				.then(function(result) {
				if(result === 'yes') {
					removeView(film);
				}
			});
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
				// Given var prio = ['fewMovies', 'shortBreak', 'longBreak', 'early']:
				// if I used the index of each element conflicts would arise: shortBreak(1) + longBreak(2) != early(3).
				// So use shifting to produce a bitmap and add them to create a non-conflicting result.
				// Also ECMA-262§11.7.1.7-8: (2 << -1) === (2 << 0xFFFFFFFFFF & 0x1F) === (2 << 31) === (0b10 << 31) === 0b10...0 [32 zeros],
				// which is 1 bit bigger than 32 bit, so it's truncated as 0 === (2 << -1), hence it's safe to use _.indexOf without any checks.
				return sum + (2 << _.indexOf(prio, offense));
			}
		};

		$scope.cleanName = function(cinemaName) {
			return cinemaName.replace("London - ", "");
		};
	}
]);
