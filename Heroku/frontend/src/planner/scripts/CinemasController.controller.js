'use strict';
var module = angular.module('appControllers'); // see app.js

module.controller('CinemasController', [
	        '$rootScope', '$scope', '_', 'Cinema',
	function($rootScope,   $scope,   _,   Cinema) {
		$scope.loading = true;
		$scope.$on('CinemasLoading', function(event) {
			$scope.loading = true;
		});
		$scope.$on('CinemasLoaded', function(event, cinemas) {
			if(!_.any(cinemas, 'selected')) {
				if(_.any(cinemas, 'fav')) {
					$scope.buttonClick($scope.buttons.favs);
				} else {
					$scope.buttonClick($scope.buttons.london);
				}
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
