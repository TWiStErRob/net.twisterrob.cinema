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

module.factory('Film', [
	        '$resource',
	function($resource) {
		return $resource('/film',
			{ 'cinemaIDs[]': [] },
			{
				list: { method: 'GET', params: { 'cinemaIDs[]': [] }, isArray: true }
			});
	}
]);

module.factory('Status', function($timeout) {
	var data = {
		stati: [],
		timeout: 2000,
		currentTimeout: undefined
	};
	return {
		showStatus: function (message, persistent) {
			if(persistent) {
				data.stati.length = 0;
				data.stati.push(message);
			} else {
				data.stati.push(message);
				if(data.currentTimeout) {
					$timeout.cancel(data.currentTimeout);
				}
				data.currentTimeout = $timeout(function() {
					data.stati.length = 0;
				}, data.timeout);
			}
		},
		getStati: function() {
			return data.stati;
		},
		setTimeout: function(timeout) {
			data.timeout = timeout;
		}
	}
});

module.service('cineworld', function($rootScope, CinemaFav, Film) {
	var data = this.data = {
		date: new Date(),
		cinemas: undefined,
		films: undefined
	};

	this.updateCinemas = function() {
		var params = {};
		$rootScope.$broadcast('CinemasLoading', cinemas);
		return data.cinemas = CinemaFav.list(params, function(cinemas) {
			angular.forEach(cinemas, function(cinema) {
				cinema.selected = cinema.fav;
			});
			$rootScope.$broadcast('CinemasLoaded', cinemas);
		});
	};

	this.updateFilms = function() {
		var params = {
			date: moment(data.date).format("YYYYMMDD"),
			cinemaIDs: data.cinemas
				.filter(function(cinema) {
					return cinema.selected;
				})
				.map(function(cinema) {
					return cinema.cineworldID;
				})
		}
		if(params.cinemaIDs.length == 0) {
			return;
		}
		$rootScope.$broadcast('FilmsLoading');
		return data.films = Film.list(params, function(films) {
			$rootScope.$broadcast('FilmsLoaded', films);
		});
	}
});
