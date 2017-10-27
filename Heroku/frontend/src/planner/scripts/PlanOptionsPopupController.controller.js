'use strict';
var module = angular.module('appControllers'); // see app.js

module.controller('PlanOptionsPopupController', [
         '$scope', '$uibModalInstance', 'options',
function( $scope,   $uibModalInstance,   options) {
	$scope.options = options;

	$scope.ok = function () {
		$uibModalInstance.close(options);
	};
	$scope.cancel = function () {
		$uibModalInstance.dismiss('cancel');
	};
}]);
