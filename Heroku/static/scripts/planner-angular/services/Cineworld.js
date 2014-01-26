'use strict';
var module = angular.module('appServices'); // see app.js

module.service('Cineworld', [
	        '$rootScope', '_', 'moment', 'Cinema', 'Film', 'Performance',
	function($rootScope,   _,   moment,   Cinema,   Film,   Performance) {
		var config = {
			cinemaWait: 0,
			filmWait: 1000,
			performanceWait: 2000
		};

		var data = this;
		data.date = new Date();
		data.date.setHours(0, 0, 0, 0);
		data.cinemas = [];
		data.films = [];
		data.performances = {};
		data.pendingSelectedCinemas = [];
		data.pendingSelectedFilms = [];

		this.updateCinemas = _.debounce(function() {
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
		}, config.cinemaWait);

		this.updateFilms = _.debounce(function() {
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
					// TODO view.date<UTC> = moment(view.date).local().toDate()
				});
				data.pendingSelectedFilms.length = 0;
				$rootScope.$broadcast('FilmsLoaded', films);
			});
		}, config.filmWait);
		
		this.updatePerformances = _.debounce(function() {
			var params = {
				date: data.formattedDate,
				cinemaIDs: data.selectedCinemaIDs,
				filmEDIs: data.selectedFilmEDIs
			};
			if(params.cinemaIDs.length === 0 || params.filmEDIs.length === 0) {
				return;
			}
			$rootScope.$broadcast('PerformancesLoading', data.performances);
			return data.performances = Performance.list(params, function(performances) {
				$rootScope.$broadcast('PerformancesLoaded', performances);
			});
		}, config.performanceWait);		

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
