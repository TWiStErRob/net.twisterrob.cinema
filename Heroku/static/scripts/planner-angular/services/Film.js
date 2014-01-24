'use strict';
var module = angular.module('appServices'); // see app.js

module.factory('Film', [
	        '$resource',
	function($resource) {
		return $resource('/film/:edi/:action',
			{
				'action': undefined,
				'edi': undefined,
				'cinemaIDs[]': undefined,
				date: undefined
			},
			{
				list: { method: 'GET', isArray: true },                     // no input required
				get: { method: 'GET' },                                     // edi is user input
				addView: { method: 'GET', params: { action: 'view' } },     // edi/cinemaIDs/date is user input
				removeView: { method: 'GET', params: { action: 'unview' } } // edi is user input
			}
		);
	}
]);
