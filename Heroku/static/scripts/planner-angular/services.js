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

module.factory('Status', [
	        '$timeout',
	function($timeout) {
		var stati = [];
		var timeout = 2000;
		var currentTimeout = undefined;
		return {
			showStatus: function (message) {
				if(currentTimeout) {
					$timeout.cancel(currentTimeout);
				}
				stati.push(message);
				currentTimeout = $timeout(function() {
					stati.length = 0;
				}, timeout);
			},
			get timeout() { return timeout; },
			set timeout(value) { timeout = value; },
			get stati() { return stati; }
		};
	}
]);

module.service('cineworld', [
	        '$rootScope', 'Cinema', 'Film',
	function($rootScope,   Cinema,   Film) {
		this.date = new Date();
		this.cinemas = undefined;
		this.films = undefined;
		this.updateCinemas = function() {
			var params = {};
			$rootScope.$broadcast('CinemasLoading', cinemas);
			return this.cinemas = Cinema.list(params, function(cinemas) {
				angular.forEach(cinemas, function(cinema) {
					cinema.selected = cinema.fav;
				});
				$rootScope.$broadcast('CinemasLoaded', cinemas);
			});
		};

		this.updateFilms = function() {
			var params = {
				date: moment(this.date).format("YYYYMMDD"),
				cinemaIDs: this.cinemas
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
			return this.films = Film.list(params, function(films) {
				$rootScope.$broadcast('FilmsLoaded', films);
			});
		};
	}
]);
