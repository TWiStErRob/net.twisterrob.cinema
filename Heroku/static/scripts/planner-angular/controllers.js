'use strict';

/* Controllers */
var module = angular.module('appControllers', []);

module.controller('AppController',
	function($scope) {
	}
);

module.controller('StatusController',
	function($scope,  Status) {
		Status.setTimeout(3000);
		$scope.stati = Status.getStati();
		$scope.$on('CinemasLoaded', function(event, cinemas) {
			Status.showStatus('Loaded ' + cinemas.length + ' cinemas.');
		})
		$scope.$on('FilmsLoaded', function(event, films) {
			Status.showStatus('Loaded ' + films.length + ' films.');
		})
		$scope.$on('CinemaFavorited', function(event, cinema) {
			Status.showStatus('Cinema favorited: ' + cinema.name);
		});
		$scope.$on('CinemaUnFavorited', function(event, cinema) {
			Status.showStatus('Cinema un-favorited: ' + cinema.name);
		});
	}
);

module.controller('DebugController',
	function($scope, cineworld) {
		$scope.cineworld = cineworld.data;
	}
);

module.controller('DateController',
	function($scope, $timeout, cineworld) {
		$scope.cineworld = cineworld.data;

		$scope.$watch('cineworld.date', function (newValue, oldValue, scope) {
			cineworld.updateFilms();
		}, true);

		$scope.open = function() {
			$timeout(function() {
				$scope.opened = true;
			});
		};
	}
);

module.controller('CinemaListController',
	function($rootScope, $scope, cineworld, Cinema) {
		$scope.cineworld = cineworld.data;

		$scope.loading = true;
		$scope.$on('CinemasLoading', function(event) {
			$scope.loading = true;
		});
		$scope.$on('CinemasLoaded', function(event, cinemas) {
			$scope.loading = false;
		});

		cineworld.updateCinemas();

		$scope.favClick = function(cinema) {
			cinema.favLoading = true;
			var params = {cinemaID: cinema.cineworldID};
			if(cinema.fav) {
				Cinema.unFav(params, function(newCinema) {
					cinema.fav = false;
					cinema.favLoading = false;
					$scope.$emit('CinemaUnFavorited', cinema);
				});
			} else {
				Cinema.fav(params, function(newCinema) {
					cinema.fav = true;
					cinema.favLoading = false;
					$scope.$emit('CinemaFavorited', cinema);
				});
			}
		}
	}
);

module.controller('FilmListController',
	function($rootScope, $scope, cineworld) {
		$scope.cineworld = cineworld.data;
		$scope.$watch('cineworld.cinemas | filter: { selected: true }', function (newValue, oldValue, scope) {
			cineworld.updateFilms();
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
);
