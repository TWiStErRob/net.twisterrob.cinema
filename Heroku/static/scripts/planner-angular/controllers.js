'use strict';
var module = angular.module('appControllers'); // see app.js

module.controller('AppController', [
	        '$rootScope', 'cineworld', '$location',
	function($scope,       cineworld,   $location) {
		$scope.cineworld = cineworld;
		$scope.$watch('cineworld.cinemas | filter: { selected: true }', function (newValue, oldValue, scope) {
			$location.search('c', _.pluck(newValue, 'cineworldID'));
			$location.replace();
		}, true);
		$scope.$watch('cineworld.films | filter: { selected: true }', function (newValue, oldValue, scope) {
			$location.search('f', _.pluck(newValue, 'edi'));
			$location.replace();
		}, true);
		$scope.$watch('cineworld.date', function (newValue, oldValue, scope) {
			$location.search('d', moment(newValue).format('YYYY-MM-DD'));
			$location.replace();
		}, true);
	}
]);

module.controller('StatusController', [
	        '$scope', 'Status',
	function($scope,   Status) {
		Status.timeout = 3000;
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

module.controller('CinemaListController', [
	        '$rootScope', '$scope', 'Cinema',
	function($rootScope,   $scope,   Cinema) {
		$scope.loading = true;
		$scope.$on('CinemasLoading', function(event) {
			$scope.loading = true;
		});
		$scope.$on('CinemasLoaded', function(event, cinemas) {
			$scope.loading = false;
		});

		$scope.cineworld.updateCinemas();

		$scope.favClick = function(cinema) {
			cinema.favLoading = true;
			var params = {cinemaID: cinema.cineworldID};
			if(cinema.fav) {
				Cinema.unFav(params).$promise.then(
					function(newCinema) {
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
					function(newCinema) {
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

module.controller('FilmListController', [
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
