'use strict';
var module = angular.module('appControllers'); // see app.js

module.controller('AppController', [
	        '$rootScope', '_', 'cineworld', '$location',
	function($scope,       _,   cineworld,   $location) {
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
	        '$scope',
	function($scope) {
	}
]);

module.controller('DateController', [
	        '$scope', '$timeout',
	function($scope,   $timeout) {
		$scope.$watch('cineworld.date', function (newValue, oldValue, scope) {
			$scope.cineworld.updateFilms();
		}, true);

		$scope.open = function() {
			$timeout(function() {
				$scope.opened = true;
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
		}

		$scope.favClick = function(cinema) {
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
		}
	}
]);

module.controller('FilmsController', [
	        '$scope',
	function($scope) {
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

		$scope.viewPopup = function(film) {}
	}
]);

module.controller('PerformancesController', [
	        '$scope',
	function($scope) {
		$scope.$watch('cineworld.films | filter: { selected: true }', function (newValue, oldValue, scope) {
			$scope.cineworld.updatePerformances();
		}, true);
		$scope.loading = true;
		$scope.$on('PerformancesLoading', function(event) {
			$scope.loading = true;
		});
		$scope.$on('PerformancesLoaded', function(event, performances) {
			//$scope.performances = _.groupBy(performances, ['cinema', 'film']);
			$scope.loading = false;
		});

		$scope.cleanName = function(cinemaName) {
			return cinemaName.replace("London - ", "");
		}
	}
]);
