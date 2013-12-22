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
		return {
			showStatus: function (message) {
				stati.push(message);
				$timeout(function() {
					stati.shift();
				}, timeout);
			},
			get timeout() { return timeout; },
			set timeout(value) { timeout = value; },
			get stati() { return stati; }
		};
	}
]);

module.service('cineworld', [
	        '$rootScope', '_', 'Cinema', 'Film',
	function($rootScope,   _,   Cinema,   Film) {
		this.date = new Date();
		this.cinemas = undefined;
		this.films = undefined;
		this.updateCinemas = function() {
			var params = {};
			$rootScope.$broadcast('CinemasLoading', cinemas);
			return this.cinemas = Cinema.list(params, function(cinemas) {
				$rootScope.$broadcast('CinemasLoaded', cinemas);
			});
		};

		this.updateFilms = function(forgetSelection) {
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
			var selectedFilms = forgetSelection? [] : _.indexBy(
				(this.films || []).filter(function(film) {
					return film.selected;
				}), 'edi'
			);
			$rootScope.$broadcast('FilmsLoading');
			return this.films = Film.list(params, function(films) {
				angular.forEach(films, function(film) {
					film.selected = film.edi in selectedFilms;
				});
				$rootScope.$broadcast('FilmsLoaded', films);
			});
		};
	}
]);
