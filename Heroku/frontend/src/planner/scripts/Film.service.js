'use strict';
var module = angular.module('appServices'); // see app.js

module.factory('Film', [
	        '$resource',
	function($resource) {
		return $resource('/film/:edi/:action',
			{
				'action': undefined,
				'edi': '@edi'
			},
			{
				list: { method: 'GET', isArray: true },                       // no input required
				get: { method: 'GET' },                                       // edi is user input
				addView: { method: 'POST', params: { action: 'view' } },      // edi/cinema/date is user input
				removeView: { method: 'DELETE', params: { action: 'view' } }, // edi is user input
				ignore: { method: 'PUT', params: { action: 'ignore' } },      // edi/reason is user input
			}
		);
	}
]);
