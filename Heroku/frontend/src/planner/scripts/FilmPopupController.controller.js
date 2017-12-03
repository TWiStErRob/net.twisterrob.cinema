'use strict';
var module = angular.module('appControllers'); // see app.js

module.controller('FilmPopupController', [
         '$scope', '$sce', '$timeout', '$uibModalInstance', 'film',
function( $scope,   $sce,   $timeout,   $uibModalInstance,   film) {
	$scope.film = film;

	$scope.dismiss = function () {
		$uibModalInstance.dismiss();
	};

	$scope.hasYouTubeTrailer = function () {
		var parser = angular.element('<a/>')[0];
		parser.href = film.trailer;
		return parser.hostname === 'www.youtube.com' || parser.hostname === 'youtu.be';
	};

	$scope.getYouTubeTrailerId = function () {
		var parser = angular.element('<a/>')[0];
		parser.href = film.trailer;
		if (parser.hostname === 'www.youtube.com') {
			return /\bv=([^&]+)/.exec(parser.search)[1];
		} else if (parser.hostname === 'youtu.be') {
			return parser.pathname.replace('/', ' ').trim();
		} else {
			return null;
		}
	};
	$scope.buildYouTubeEmbedUrl = function (videoId) {
		return $sce.trustAsResourceUrl('https://www.youtube-nocookie.com/embed/' + videoId + '?rel=0');
	};
}]);
