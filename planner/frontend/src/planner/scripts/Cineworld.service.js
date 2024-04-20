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
			data.cinemas = Cinema.list(params, function(cinemas) {
				angular.forEach(cinemas, function(cinema) {
					cinema.selected = _.includes(data.pendingSelectedCinemas, cinema.cineworldID);
				});
				data.pendingSelectedCinemas.length = 0;
				$rootScope.$broadcast('CinemasLoaded', cinemas);
			});
			return data.cinemas;
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
			data.films = Film.list(params, function(films) {
				angular.forEach(films, function(film) {
					film.selected = _.includes(data.pendingSelectedFilms, film.edi);
					// TODO view.date<UTC> = moment(view.date).local().toDate()
				});
				data.pendingSelectedFilms.length = 0;
				$rootScope.$broadcast('FilmsLoaded', films);
			});
			return data.films;
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
			data.performances = Performance.list(params, function(performances) {
				$rootScope.$broadcast('PerformancesLoaded', performances);
			});
			return data.performances;
		}, config.performanceWait);

		Object.defineProperty(data, 'selectedCinemas', {
			get: function() { return _.filter(data.cinemas, 'selected'); }
		});
		Object.defineProperty(data, 'selectedCinemaIDs', {
			get: function() { return _.map(data.selectedCinemas, 'cineworldID'); }
		});
		Object.defineProperty(data, 'selectedFilms', {
			get: function() { return _.filter(data.films, 'selected'); }
		});
		Object.defineProperty(data, 'selectedFilmEDIs', {
			get: function() { return _.map(data.selectedFilms, 'edi'); }
		});
		Object.defineProperty(data, 'formattedDate', {
			get: function() { return moment(data.date).format("YYYYMMDD"); }
		});
		Object.defineProperty(data, 'dateIsToday', {
			get: function() { return moment(data.date).isSame(moment().add(0, 'day'), 'date'); }
		});
		Object.defineProperty(data, 'dateIsTomorrow', {
			get: function() { return moment(data.date).isSame(moment().add(1, 'day'), 'date'); }
		});
		Object.defineProperty(data, 'dateInPast', {
			get: function() { return moment(data.date).isBefore(moment().startOf('day')); }
		});
	}
]);
