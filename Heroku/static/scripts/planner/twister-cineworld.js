"use strict";
twister.core.namespaces.create("twister.cineworld");
var NS = twister.core.namespaces.extend;

twister.cineworld = NS(twister.cineworld, {
	consts: {
		apiKey: "9qfgpF7B"
	},
	performances: {
		// [index by date][index by cinema][index by filmEdi]
	},
	getFavoriteCinemas: function() {
		return $.ajax({
				url: twister.config.localUrlBase + '/cinema/favs'
			})
			.then(function cineworld_parseFavoriteCinemas(response, status, xhr) {
				response = twister.utils.clean(response);
				return $.map(response, function(fav) { return fav.cineworldID; });
			})
		;
	},
	addCinemaFav: function(id) {
		return $.ajax({
			url: twister.config.localUrlBase + '/cinema/' + id + '/fav'
		})
		.then(function cineworld_parseFavoriteCinema(response, status, xhr) {
			return response.cinema.name;
		});
	},
	removeCinemaFav: function(id) {
		return $.ajax({
			url: twister.config.localUrlBase + '/cinema/' + id + '/unfav'
		})
		.then(function cineworld_parseUnFavoriteCinema(response, status, xhr) {
			return response.cinema.name;
		});
	},
	getCinemas: function() {
		$('#cinemas').empty();
		twister.ui.showStatus("Loading cinemas...");
		return $.ajax({
			url: 'http://www.cineworld.com/api/quickbook/cinemas',
			data: {
				key: twister.cineworld.consts.apiKey,
				full: true
			}
		})
		.then(twister.utils.unPackArrayPromise)
		.then(twister.cineworld.parseCinemasResponse)
		;
	},
	parseCinemasResponse: function(response) {
		/*
		address: "Queens Links Leisure Park, Links Road, Aberdeen"
		cinema_url: "http://www.cineworld.co.uk/cinemas/1/information"
		id: 1
		name: "Aberdeen - Queens Links"
		postcode: "AB24 5EN"
		telephone: "0871 200 2000"
		*/
		var dfd = $.Deferred();
		// Check for errors from the server
		if (response.errors) {
			dfd.reject(response);
		} else {
			dfd.resolve(response.cinemas);
		}
		return dfd;
	},
	getFilms: function(cinemaIds, date) {
		var request = {
			key: twister.cineworld.consts.apiKey,
			full: true,
			cinema: cinemaIds,
			date: date
		};
		if(request.cinema.length == 0) {
			var dfd = new jQuery.Deferred();
			dfd.resolve([]);
			return dfd.promise();
		}

		var quickbookRequest = $.ajax({
			url: 'http://www.cineworld.com/api/quickbook/films',
			data: request
		});
		var apiRequest = $.ajax({
			url: 'http://www.cineworld.com/api/film/list',
			data: request
		});

		return $.when(quickbookRequest, apiRequest)
			.then(twister.utils.unPackAjaxPromises)
			.then(twister.cineworld.mergeResponses)
			.then(twister.cineworld.parseMergedResponse)
		;
	},
	mergeResponses: function(quickbook, internal) {
		var merged = {
			quickbook: quickbook,
			internal: internal,
			films: {},
			errors: []
		};

		// Preprocess
		$.each(internal.films, function() {
			this.poster = internal.base_url + this.poster;
		});

		// Films
		function local_addResponseByEdi(type) {
			return function() {
				merged.films[this.edi] = merged.films[this.edi] || {};
				merged.films[this.edi][type] = this;
			};
		}
		$.each(quickbook.films, local_addResponseByEdi('quickbook'));
		$.each(internal.films, local_addResponseByEdi('internal'));
		$.each(merged.films, function(index) {
			merged.films[index] = $.extend(true, this, this.quickbook, this.internal);
		});

		// Errors
		$.extend(merged.errors, quickbook.errors, internal.errors);
		if(merged.errors.length == 0) {
			delete merged.errors;
		}

		// Final processing
		merged.films = $.map(merged.films, function(film) { return film; });
		return twister.cineworld.films.responses = merged;
	},
	parseMergedResponse: function(mergedResponse) {
		/*
		http://www.cineworld.com/api/quickbook/films:
		3D: false
		classification: "15"
		edi: 63829
		film_url: "http://www.cineworld.co.uk/whatson/6237"
		id: 6237
		imax: false
		poster_url: "http://www.cineworld.co.uk/assets/media/films/6237_poster.jpg"
		still_url: "http://www.cineworld.co.uk/assets/media/films/6237_still.jpg"
		title: "This Is The End"
		+ http://www.cineworld.co.uk/api/film/detail?key=9qfgpF7B&film=6237

		http://www.cineworld.com/api/film/list:
		actors: "Steve Carell, Al Pacino, Kristen Wiig, Ken Jeong, Steve Coogan, Russell Brand, Miranda Cosgrove"
		cert: "U"
		edi: 43599
		id: 6322
		length: 98
		poster: "/assets/media/films/6235_poster.jpg"
		release: "20130628"
		title: "2D - Despicable Me 2"
		trailer: "http://webcache1.bbccustomerpublishing.com/cineworld/trailers/Despicable Me 2 _qtp.mp4"
		weighted: 65
		*/
		var dfd = $.Deferred();
		// Check for errors from the server
		if (mergedResponse.errors) {
			dfd.reject(mergedResponse);
		} else {
			dfd.resolve(mergedResponse.films);
		}
		return dfd;
	},
	getFilmLengths: function(films) {
		var filmLengthRequests = new Array();
		filmLengthRequests.push({
			whenHack: "when returns the promise if there's only one, this object helps to create an array of deferreds"
		});
		$.each(films, function film_getLength() {
			filmLengthRequests.push(twister.cineworld.getFilmLength(this));
		});
		return $.when.apply($, filmLengthRequests)
			.then(function getFilmLengths_processFilmLengths(/*...*/) {
				// TODO twister.cineworld.plan(); replan, when film lengths are done, results may have changed
				var responses = Array.prototype.slice.call(arguments, 1);
				for(var i = 0; i < responses.length; ++i) {
					var response = responses[i];
					var film = response[0];
					var runtime = response[1];
					var error = response[2];
					if(response.length == 2) { // [film, runtime]
						film.length = runtime;
					} else if (response.length == 3) { // [film, runtime, error]
						console.warn("'{0}' has invalid runtime: '{1}' ({2})".format(film.title, runtime, error));
						film.length = 100;
					} else { // something terribly wrong
						console.error("Invalid arguments #" + i + ": " + response);
					}
				}
				return $.map(responses, function(response) { return response[0]; });
			})
		;
	},
	getFilmLength: function(film) {
		var dfd = $.Deferred(twister.utils.bindDeferredThis(film));
		if(film.length) { // from Cineworld API
			dfd.resolve(film, film.length);
		} else if(film.edi in twister.cineworld.films.lengths) { // cache
			dfd.resolve(film, twister.cineworld.films.lengths[film.edi]);
		} else {
			$.ajax({
				url: 'http://deanclatworthy.com/imdb/',
				data:  {
					q: film.title.replace(/^\s*(IMAX)?\s*[23]D\s*-\s*/gi, ""), // remove 2D/3D/IMAX qualifiers
					type: 'jsonp'
				}
			})
				.then(twister.utils.unPackArrayPromise)
				.done(function getFilmLength_parseResponse(response, status, xhr) {
					// http://deanclatworthy.com/imdb/?q=World%20War%20Z&type=jsonp&callback=callback
					/* callback({
						imdbid: "tt0816711", 
						imdburl: "http://www.imdb.com/title/tt0816711/", 
						genres: "Action,Drama,Horror,Sci-Fi,Thriller", 
						languages: "English", 
						country: "USA,Malta", 
						votes: "67357", 
						stv: 0, 
						series: 0, 
						rating: "7.3", 
						runtime: "116min", 
						title: "World War Z", 
						year: "2013", 
						usascreens: 3607, 
						ukscreens: 0, 
						cacheExpiry: 1374104898
					}) */
					if(response.error) {
						dfd.resolve(film, undefined, response.error);
					} else if(response.runtime == 0 || !$.isNumeric(parseInt(response.runtime))) {
						dfd.resolve(film, response.runtime, "0 or not numeric");
					} else {
						dfd.resolve(film, parseInt(response.runtime));
					}
				})
				.fail(function(xhr, status, error) {
					dfd.resolve(film, undefined, error);
				})
			;
		}
		return dfd.promise()
			.done(function(film, runtime, error) {
				if(error === undefined) {
					twister.cineworld.films.lengths[film.edi] = runtime;
				}
			})
		;
	},
	getPerformance: function(args) {
		var request = {
			key: twister.cineworld.consts.apiKey,
			cinema: args.cinemaId,
			film: args.filmEdi,
			date: twister.cineworld.getDate()
		};
		var perfs = twister.cineworld.performances;
		perfs[request.date] = perfs[request.date] || {};
		perfs[request.date][request.cinema] = perfs[request.date][request.cinema] || {};
		var perf = perfs[request.date][request.cinema][request.film];
		var dfd = $.Deferred();
		if(perf && new Date().getTime() - perf.retrieved.getTime() < 10 * 60 * 1000) { // use cache for 10 minutes
			dfd.resolve(perf);
		} else {
			/* {
					"performances":[
						{"time":"13:00","available":true,"type":"reg","ad":true,"subtitled":false,"ss":false,"booking_url":"http://www.cineworld.co.uk/booking?performance=976170&key=9qfgpF7B"},
						{"time":"15:30","available":true,"type":"reg","ad":true,"subtitled":false,"ss":false,"booking_url":"http://www.cineworld.co.uk/booking?performance=976178&key=9qfgpF7B"},
						{"time":"18:10","available":true,"type":"reg","ad":true,"subtitled":false,"ss":false,"booking_url":"http://www.cineworld.co.uk/booking?performance=976186&key=9qfgpF7B"},
						{"time":"20:40","available":true,"type":"reg","ad":true,"subtitled":false,"ss":false,"booking_url":"http://www.cineworld.co.uk/booking?performance=976194&key=9qfgpF7B"}
					],
					"legends":[{"code":"reg","description":"Regular"}]
				} */
			$.ajax({
				url: 'http://www.cineworld.com/api/quickbook/performances',
				data: request
			})
			.then(twister.utils.unPackArrayPromise)
			.done(function getPerformance_ajax_done(response, status, xhr) {
				$.extend(true, response, {
					cinema: request.cinema,
					film: request.film,
					date: request.date,
					retrieved: new Date()
				});
				if(response.errors) {
					dfd.reject(response);
				} else {
					dfd.resolve(response);
				}
			})
			.fail(function getPerformance_ajax_fail(xhr, status, error) {
				dfd.reject(error);
			});
		}
		dfd.done(function getPerformance_cache(response) {
			twister.cineworld.performances[response.date][response.cinema][response.film] = response;
		});
		return dfd.promise();
	}
});
