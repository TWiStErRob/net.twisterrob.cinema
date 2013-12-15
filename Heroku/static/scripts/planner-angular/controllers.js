'use strict';

/* Controllers */
var module = angular.module('appControllers', []);

module.controller('AppController',
	function($scope,   $timeout) {
		$scope.$on('CinemasLoaded', function(event, cinemas) {
			showStatus('Loaded ' + cinemas.length + ' cinemas.');
		})
		$scope.$on('FilmsLoaded', function(event, films) {
			showStatus('Loaded ' + films.length + ' films.');
		})
		$scope.$on('CinemaFavorited', function(event, cinema) {
			showStatus('Cinema favorited: ' + cinema.name);
		});
		$scope.$on('CinemaUnFavorited', function(event, cinema) {
			showStatus('Cinema un-favorited: ' + cinema.name);
		});

		function showStatus(message, persistent) {
			$scope.status = message;
			if(!persistent) {
				$timeout(function() {
					$scope.status = undefined;
				}, 2000);
			}
		}
	}
);

module.controller('DebugController',
	function($scope, cineworld) {
		$scope.cineworld = cineworld.data;
	}
);

module.controller('CinemaListController',
	function($rootScope, $scope, cineworld, Cinema) {
		$scope.cinemas = cineworld.updateCinemas();

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
		$scope.$on('CinemasLoaded', function(event, cinemas) {
			$scope._cinemas = cinemas;
		})
		$scope.$watch('_cinemas | filter: { selected: true }', function (newValue, oldValue, scope) {
			$scope.films = cineworld.updateFilms();
		}, true);

		$scope.viewPopup = function(film) {}
	}
);
