'use strict';
var module = angular.module('appControllers'); // see app.js

module.controller('AppController', [
	        '$rootScope', '_', '$window', '$uibModal', 'moment', 'Cineworld', '$location',
	function($scope,       _,   $window,   $uibModal,   moment,   cineworld,   $location) {
		$scope.Math = $window.Math;
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
			if (cinemaIDs.length) {
				$scope.cineworld.pendingSelectedCinemas.length = 0;
			}
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
			if (filmEDIs.length) {
				$scope.cineworld.pendingSelectedFilms.length = 0;
			}
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

		$scope.filmDetailsPopup = function(film) {
			var modalInstance = $uibModal.open({
				templateUrl: 'templates/filmPopup.html',
				windowClass: 'modal-lg',
				controller: 'FilmPopupController',
				resolve: {
					film: function() {
						return film;
					}
				}
			});
		};
	}
]);

module.run(['$templateCache', function ($templateCache) {
	$templateCache.put('templates/filmPopup.html', require('../templates/filmPopup.html'));
}]);
