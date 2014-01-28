'use strict';
var module = angular.module('appControllers'); // see app.js

module.controller('DateController', [
	        '$scope', '$timeout',
	function($scope,   $timeout) {
		$scope.$watch('cineworld.date', function (newValue, oldValue, scope) {
			$scope.cineworld.updateFilms();
		}, true);

		$scope.cineworldDatePickerDisplayed = false;
		$scope.displayCineworldDatePicker = function() {
			$timeout(function() {
				$scope.cineworldDatePickerDisplayed = true;
			});
		};
	}
]);
