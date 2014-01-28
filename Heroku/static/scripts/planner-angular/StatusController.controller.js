'use strict';
var module = angular.module('appControllers'); // see app.js

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
