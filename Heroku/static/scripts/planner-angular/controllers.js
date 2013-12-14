'use strict';

/* Controllers */
var module = angular.module('appControllers', []);

module.controller('CinemaListController',
	       ['$scope', 'CinemaFav',
	function($scope,   Cinema) {
	$scope.cinemas = Cinema.list({});
}]);
