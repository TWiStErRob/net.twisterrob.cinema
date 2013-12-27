'use strict';
var module = angular.module('appServices'); // see app.js

module.factory('Performance', [
	        '$resource',
	function($resource) {
		return $resource('/performance',
			{
				'cinemaIDs': [],
				'filmEDIs': [],
				'date': undefined
			},
			{
				list: { method: 'GET', isArray: true }
			}
		);
	}
]);
