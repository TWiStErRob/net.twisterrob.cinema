'use strict';
var module = angular.module('appControllers'); // see app.js

module.controller('ViewPopupController', [
         '$scope', '$timeout', '$uibModalInstance', 'moment', 'cinemas', 'defaultCinema', 'films', 'defaultFilm', 'defaultDate',
function( $scope,   $timeout,   $uibModalInstance,   moment,   cinemas,   defaultCinema,   films,   defaultFilm,   defaultDate) {
	$scope.cinemas = cinemas;
	$scope.films = films;
	$scope.selected = {
		cinema: defaultCinema,
		film: defaultFilm,
		date: defaultDate,
		time: defaultDate,
		newFriend: "",
		friends: []
	};

	$scope.cinemaGroup = function(cinema) {
		return cinema.fav ? "Favorites" : "Others";
	};
	$scope.filmGroup = function(film) {
		return film.view ? "Watched" : "New";
	};

	$scope.uiState = {
		datePickerDisplayed: false,
		displayDatePicker: function() {
			$timeout(function() {
				// TODO figure out why doesn't it work it's supposed to (location of function doesn't matter):
				// 1. doesn't work without $digest
				// 2. doesn't work without uiState intermediate object (not even with $digest)
				$scope.uiState.datePickerDisplayed = true;
				$scope.$digest();
			});
		}
	};

	$scope.ok = function(reason) {
		var sel = $scope.selected;
		var date = moment(sel.date).startOf('day').valueOf(); // strip time
		var time = sel.time - moment(sel.time).startOf('day'); // strip date
		$uibModalInstance.close({
			cinema: sel.cinema,
			film: sel.film,
			date: date + time,
			friends: sel.friends,
			ignore: reason
		});
	};
	$scope.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
	$scope.ignore = function(reason) {
		$scope.ok(reason);
	};
}]);
