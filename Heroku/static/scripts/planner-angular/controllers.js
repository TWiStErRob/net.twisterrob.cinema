'use strict';

/* Controllers */
var module = angular.module('appControllers', []);

module.controller('CinemaListController',
	       ['$scope', 'Cinema',
	function($scope,   Cinema) {
	Cinema.list({}, function(response) {
		$scope.cinemas = response.cinemas;
	});
}]);
