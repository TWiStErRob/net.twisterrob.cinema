'use strict';
var module = angular.module('appServices'); // see app.js

module.factory('Cinema', [
	        '$resource',
	function($resource) {
		return $resource('/cinema/:cinemaID/:action',
			{ action: undefined, cinemaID: undefined },
			{
				list: { method: 'GET', isArray: true },
				fav: { method: 'GET', params: { action: 'fav' } },
				unFav: { method: 'GET', params: { action: 'unfav' } }
			}
		);
	}
]);
