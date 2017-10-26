'use strict';
var module = angular.module('appControllers'); // see app.js

module.controller('PlanOptionsPopupController', [
         '$scope', '$modalInstance', 'options',
function( $scope,   $modalInstance,   options) {
	$scope.options = options;

	$scope.ok = function () {
		$modalInstance.close(options);
	};
	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};
}]);
