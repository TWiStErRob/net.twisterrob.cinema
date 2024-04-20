'use strict';
var module = angular.module('appServices'); // see app.js

module.factory('Cinema', [
	        '$resource',
	function($resource) {
		return $resource('/cinema/:cinemaID/:action',
			{
				'action': undefined,
				'cinemaID': '@cinemaID'
			},
			{
				list: { method: 'GET', isArray: true },
				fav: { method: 'PUT', params: { action: 'favorite' } },     // cinemaID is input
				unFav: { method: 'DELETE', params: { action: 'favorite' } } // cinemaID is input
			}
		);
	}
]);

