'use strict';
var module = angular.module('appServices'); // see app.js

module.factory('Film', [
	        '$resource',
	function($resource) {
		return $resource('/film/:edi/:action',
			{ 'cinemaIDs[]': [], 'edi': undefined, 'action': undefined },
			{
				list: { method: 'GET', isArray: true },                // no input required
				get: { method: 'GET' },                                // edi is user input
				addView: { method: 'GET', params: { action: 'view' } } // edi is user input
			}
		);
	}
]);
