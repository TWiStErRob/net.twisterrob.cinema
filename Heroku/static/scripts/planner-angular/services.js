'use strict';

/* Services */
var module = angular.module('appServices', ['ngResource']);

module.factory('Cinema', [
	        '$resource',
	function($resource) {
		var apiKey = "9qfgpF7B";

		return $resource('http://www.cineworld.com/api/quickbook/cinemas',
			{ key: apiKey, full: true, callback: 'JSON_CALLBACK' },
			{
				list: { method: 'JSONP' }
			});
	}
]);
