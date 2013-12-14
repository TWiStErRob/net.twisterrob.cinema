'use strict';

/* Controllers */
var module = angular.module('appControllers', []);

module.controller('CinemaListController',
	       ['$rootScope', '$scope', 'CinemaFav',
	function($rootScope,   $scope,   Cinema) {
		$rootScope.cinemas = Cinema.list({}, function(cinemas) {
			$scope.$emit('CinemasLoaded', cinemas);
		});
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
]);

module.controller('AppController',
	       ['$scope', '$timeout',
	function($scope,   $timeout) {
		$scope.$on('CinemasLoaded', function(event, cinemas) {
			showStatus('Loaded ' + cinemas.length + ' cinemas.');
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
]);
