'use strict';
var module = angular.module('appControllers'); // see app.js

module.controller('FilmPopupController', function($scope, $timeout, $modalInstance, film) {
	$scope.film = film;

	$scope.dismiss = function () {
		$modalInstance.dismiss();
	};
});
