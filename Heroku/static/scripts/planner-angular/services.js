'use strict';

/* Services */
var module = angular.module('appServices', ['ngResource']);

module.factory('Cinema', [
	        '$resource',
	function($resource) {
		var apiKey = "9qfgpF7B";

		/* cinema = {
			id: 1
			name: "Aberdeen - Queens Links"
			cinema_url: "http://www.cineworld.co.uk/cinemas/1/information"
			address: "Queens Links Leisure Park, Links Road, Aberdeen"
			postcode: "AB24 5EN"
			telephone: "0871 200 2000"
		} */
		return $resource('http://www.cineworld.com/api/quickbook/cinemas',
			{ key: apiKey, full: true, callback: 'JSON_CALLBACK' },
			{
				list: { method: 'JSONP' }
			});
	}
]);

module.factory('CinemaFav', [
	        '$resource',
	function($resource) {
		return $resource('/cinema/:cinemaID/:action',
			{ action: 'SET_ME', cinemaID: -1 },
			{
				list: { method: 'GET', params: { action: undefined, cinemaID: undefined }, isArray: true },
				fav: { method: 'GET', params: { action: 'fav' } },
				unFav: { method: 'GET', params: { action: 'unfav' } }
			});
	}
]);
