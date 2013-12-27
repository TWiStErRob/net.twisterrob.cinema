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

module.factory('Performance', [
	        '$resource',
	function($resource) {
		return $resource('/performance',
			{
				'cinemaIDs': [],
				'filmEDIs': [],
				'date': undefined
			},
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
			showStatus: function addBottomMessage(message) {
				stati.push(message); // ad new message to the queue
				$timeout(function removeTopMessage() {
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
	        '$rootScope', '_', 'Cinema', 'Film', 'Performance',
	function($rootScope,   _,   Cinema,   Film,   Performance) {
		var data = this;
		data.date = new Date();
		data.date.setHours(0, 0, 0, 0);
		data.cinemas = [];
		data.films = [];
		data.performances = {};
		data.pendingSelectedCinemas = [];
		data.pendingSelectedFilms = [];

		this.updateCinemas = function() {
			var params = {
				// no params for cinemas
			};
			$rootScope.$broadcast('CinemasLoading', data.cinemas);
			return data.cinemas = Cinema.list(params, function(cinemas) {
				angular.forEach(cinemas, function(cinema) {
					cinema.selected = _.contains(data.pendingSelectedCinemas, cinema.cineworldID);
				});
				data.pendingSelectedCinemas.length = 0;
				$rootScope.$broadcast('CinemasLoaded', cinemas);
			});
		};

		this.updateFilms = function() {
			var params = {
				date: data.formattedDate,
				cinemaIDs: data.selectedCinemaIDs
			};
			if(params.cinemaIDs.length === 0) {
				return;
			}
			$rootScope.$broadcast('FilmsLoading', data.films);
			return data.films = Film.list(params, function(films) {
				angular.forEach(films, function(film) {
					film.selected = _.contains(data.pendingSelectedFilms, film.edi);
				});
				data.pendingSelectedFilms.length = 0;
				$rootScope.$broadcast('FilmsLoaded', films);
			});
		};

		this.updatePerformances = function() {
			var params = {
				date: data.formattedDate,
				cinemaIDs: data.selectedCinemaIDs,
				filmEDIs: data.selectedFilmEDIs
			}
			if(params.cinemaIDs.length === 0 || params.filmEDIs.length === 0) {
				return;
			}
			$rootScope.$broadcast('PerformancesLoading', data.performances);
			return data.performances = Performance.list(params, function(performances) {
				$rootScope.$broadcast('PerformancesLoaded', performances);
			});
		};

		Object.defineProperty(data, 'selectedCinemas', {
			get: function() { return _.filter(data.cinemas, _.fn.prop('selected')); }
		});
		Object.defineProperty(data, 'selectedCinemaIDs', {
			get: function() { return _.map(data.selectedCinemas, _.fn.prop('cineworldID')); }
		});
		Object.defineProperty(data, 'selectedFilms', {
			get: function() { return _.filter(data.films, _.fn.prop('selected')); }
		});
		Object.defineProperty(data, 'selectedFilmEDIs', {
			get: function() { return _.map(data.selectedFilms, _.fn.prop('edi')); }
		});
		Object.defineProperty(data, 'formattedDate', {
			get: function() { return moment(data.date).format("YYYYMMDD"); }
		});
	}
]);
