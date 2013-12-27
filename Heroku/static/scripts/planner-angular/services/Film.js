'use strict';
var module = angular.module('appServices'); // see app.js

module.factory('Film', [
	        '$resource',
	function($resource) {
		return $resource('/film',
			{ 'cinemaIDs[]': [] },
			{
				list: { method: 'GET', isArray: true }
			}
		);
	}
]);
