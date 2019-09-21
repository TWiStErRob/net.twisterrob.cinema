'use strict';
var module = angular.module('appControllers'); // see app.js

module.controller('PlanOptionsPopupController', [
         '$scope', '$uibModalInstance', 'moment', 'options',
function( $scope,   $uibModalInstance,   moment,   options) {
	$scope.options = {
		minWaitBetweenMovies: options.minWaitBetweenMovies.as('minutes'),
		maxWaitBetweenMovies: options.maxWaitBetweenMovies.as('minutes'),
		dealbreakingWaitBetweenMovies: options.dealbreakingWaitBetweenMovies.as('minutes'),
		estimatedAdvertisementLength: options.estimatedAdvertisementLength.as('minutes'),
		endOfWork: new Date(options.endOfWork.as('milliseconds'))
	};
	$scope.result = function() {
		const options = $scope.options;
		return {
			minWaitBetweenMovies: moment.duration(options.minWaitBetweenMovies, 'minutes'),
			maxWaitBetweenMovies: moment.duration(options.maxWaitBetweenMovies, 'minutes'),
			dealbreakingWaitBetweenMovies: moment.duration(options.dealbreakingWaitBetweenMovies, 'minutes'),
			estimatedAdvertisementLength: moment.duration(options.estimatedAdvertisementLength, 'minutes'),
			endOfWork: moment.duration(options.endOfWork.valueOf(), 'milliseconds')
		};
	};

	$scope.ok = function () {
		$uibModalInstance.close($scope.result());
	};
	$scope.cancel = function () {
		$uibModalInstance.dismiss('cancel');
	};
}]);
