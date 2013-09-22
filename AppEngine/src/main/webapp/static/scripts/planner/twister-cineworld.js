"use strict";
twister.core.namespaces.create("twister.cineworld");
var NS = twister.core.namespaces.extend;

twister.cineworld = NS(twister.cineworld, {
	const: {
		apiKey: "9qfgpF7B",
		defaults: {
			adLength: 15,
			breakLength: 5
		}
	},
	cinemas: {
		all: [ /* retrieved from cineworld API */ ],
		selectedIds: [ /* update when checklist checked */ ],
		retrieveFilmsDelay: new twister.utils.DelayedExecutor({ timeout: 300 })
	},
	films: {
		all: [ /* retrieved from cineworld API */ ],
		selectedIds: [ /* update when checklist checked */ ],
		responses: {
			// quickbook: { /* response from quickbook API */ }
			// internal: { /* response from internal API */ }
			// merged: { /* merged previous responses */ }
		},
		lengths: { /* cache for film lengths */ },
		getPerformancesDelay: new twister.utils.DelayedExecutor({ timeout: 300 })
	},
	performances: {
		// [index by date][index by cinema][index by id]
	},
	planner: {},
	date: moment().add('days', 0).format("YYYY-MM-DD"),
	getDate: function cineworld_getDate() {
		return twister.cineworld.date.replace(/-/g, '');
	},
	rebuildUrl: function rebuildUrl() {
		$("#url").attr("href",
			location.protocol + '//' + location.host + location.pathname
			+ '?' + twister.cineworld.getArgs());
	},
	updateFromQuery: function updateFromQuery() {
		twister.config.debug = !!twister.utils.url.getQueryParam('debug') || twister.config.debug;
		twister.cineworld.date = twister.utils.url.getQueryParam('date') || twister.cineworld.date;
		twister.cineworld.films.selectedIds = twister.utils.url.getQueryParams('film') || twister.cineworld.films.selectedIds;
		twister.cineworld.cinemas.selectedIds = twister.utils.url.getQueryParams('cinema') || twister.cineworld.cinemas.selectedIds;
	},
	writeUI: function() {
		twister.ui.screen.date.val(twister.cineworld.date);
	},
	getArgs: function() {
		var args = 'date=' + encodeURIComponent(twister.cineworld.date);
		args += '&' + $.map(twister.cineworld.cinemas.selectedIds, function(e) {return 'cinema=' + e; }).join('&');
		args += '&' + $.map(twister.cineworld.films.selectedIds, function(e) {return 'film=' + e; }).join('&');
		return args;
	},
	getFavoriteCinemas: function cineworld_getFavoriteCinemas() {
		return $.ajax({
				url: twister.config.localUrlBase + '/rest/cinemas/favorites'
			})
			.then(function cineworld_parseFavoriteCinemas(response, status, xhr) {
				response = twister.utils.clean(response);
				return $.map(response, function(fav) { return fav.cinemaId; });
			})
		;
	},
	getCinemas: function cineworld_getCinemas() {
		$('#cinemas').empty();
		twister.ui.showStatus("Loading cinemas...");
		return $.ajax({
			url: 'http://www.cineworld.com/api/quickbook/cinemas',
			data: {
				key: twister.cineworld.const.apiKey,
				full: true
			}
		})
		.then(twister.utils.unPackArrayPromise)
		.then(twister.cineworld.parseCinemasResponse)
		;
	},
	saveCinemas: function cineworld_saveCinemas(cinemas) {
		var cinemasMap = {};
		$.each(cinemas, function() {
			cinemasMap[this.id] = this;
		});
		twister.cineworld.cinemas.all = cinemasMap;
	},
	parseCinemasResponse: function cineworld_parseCinemasResponse(response) {
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
	displayCinemas: function cineworld_displayCinemas(cinemas) {
		var cinemasList = $('<select id="cinemas" name="cinemas" multiple="multiple"></select>');
		$.each(cinemas, function() {
			var option = new Option(this.name, this.id);
			$(option).attr('title', JSON.stringify(this, null, '  '));
			cinemasList.append(option);
		});

		$('#cinemas').replaceWith(cinemasList); // toChecklist needs the <select> to be part of DOM
		cinemasList.toChecklist( {
			showSelectedItems: false,
			addSearchBox: false,
			addScrollBar : false,
			preferIdOverName: false,
			itemWidth: 200,
			searchBoxText: 'Filter by cinema name here...',
			animateSearch: 0,
			onItemChanged: function cinemasList_onItemChanged() {
				var checkedCinemas = $("li :checkbox:checked", "#cinemas");
				var checkedCinemaIds = $.map(checkedCinemas, function(e) { return e.value; });
				twister.cineworld.cinemas.selectedIds = checkedCinemaIds;
				twister.cineworld.rebuildUrl();

				twister.cineworld.cinemas.retrieveFilmsDelay.start();
			},
			onItemSelected: function cinemasList_onItemSelected(input) {
				var request = {
					cinema: input.value,
					rating: 100,
					displayOrder: 1
				};
				var cinemaName = $(arguments[0]).siblings("label").text();
				return; // TODO enable again
				$.ajax({
					url: twister.config.localUrlBase + '/rest/cinemas/favorite',
					data: request,
					success: function(response, status, xhr) {
						twister.ui.showStatus("Cinema #" + input.value + ": " + cinemaName + " successfully favorited.");
					},
					error: function(xhr, status, error) {
						twister.ui.showStatus("Cannot favorite cinema #" + input.value + ": " + cinemaName + " - " + status + ", " + error);
					}
				});
			}
		} );
		twister.cineworld.cinemas.retrieveFilmsDelay.updateConfig({
			callback: function() {
				twister.cineworld.retrieveFilms()
					.done(twister.cineworld.saveFilms)
					.done(function (films) {
						twister.ui.showStatus("Got " + films.length + " films.");
					})
					.fail(function (responseWithErrors) {
						// response.errors is not empty.
						twister.ui.showStatus("Failure getting films: " + JSON.stringify(response, null, '  '));
					})
					.done(twister.cineworld.displayFilms)
				;
			}
		});
		return $('#cinemas');
	},
	retrieveFilms: function cineworld_retrieveFilms() {
		var cinemaIds = twister.cineworld.cinemas.selectedIds;
		var date = twister.cineworld.getDate();
		return twister.cineworld.getFilms(cinemaIds, date)
			.done(twister.cineworld.getFilmLengths)
		;
	},
	getFilms: function cineworld_getFilms(cinemaIds, date) {
		var request = {
			key: twister.cineworld.const.apiKey,
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
	mergeResponses: function cineworld_mergeResponses(quickbook, internal) {
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
	parseMergedResponse: function cinewolrd_parseFilmsResponse(mergedResponse) {
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
	saveFilms: function cineworld_saveFilms(films) {
		var filmsMap = {};
		$.each(films, function() {
			filmsMap[this.edi] = this;
		});
		twister.cineworld.films.all = filmsMap;
	},
	displayFilms: function cineworld_displayFilms(films) {
		films.sort(function(a, b) { return a.title.toUpperCase().localeCompare(b.title.toUpperCase()); });

		var filmsList = $('<select id="films" name="films" multiple="multiple"></select>');
		$.each(films, function() {
			var title = this.title + ' (<span class="runtime">' + (this.length !== undefined? this.length : '?') + '</span>)';
			var option = new Option(title, this.edi);
			$(option).attr('title', JSON.stringify(this, null, '  '));
			filmsList.append(option);
		});

		$('#films').replaceWith(filmsList); // toChecklist needs the <select> to be part of DOM
		filmsList.toChecklist( {
			showSelectedItems: false,
			addScrollBar : false,
			addSearchBox: true,
			useHTMLLabels: true,
			preferIdOverName: false,
			searchBoxText: 'Filter by film title here...',
			animateSearch: 0,
			onItemChanged: function filmsList_onItemChanged() {
				var checkedFilms = $("li :checkbox:checked", "#films");
				var checkedFilmEdis = $.map(checkedFilms, function(e) { return e.value; });
				twister.cineworld.films.selectedIds = checkedFilmEdis;
				twister.cineworld.rebuildUrl();

				twister.cineworld.films.getPerformancesDelay.start();
			}
		} );
		twister.cineworld.films.getPerformancesDelay.updateConfig({
			callback: function() {
				twister.cineworld.getPerformances();
			}
		});
		$.each(twister.cineworld.films.selectedIds, function film_select() {
			var filmElem = $('#films li:has(:checkbox[value=' + this + '])');
			if(filmElem.length == 0) {
				console.warn("Film disappeared: " + this);
			} else {
				filmElem.click();
			}
		});
		return films;
	},
	getFilmLengths: function cineworld_getFilmLengths(films) {
		var filmLengthRequests = new Array();
		$.each(films, function film_getLength() {
			filmLengthRequests.push(twister.cineworld.getFilmLength(this));
		});
		$.when.apply($, filmLengthRequests)
			.then(function(/*...*/) {
				// TODO twister.cineworld.plan(); replan, when film lengths are done, results may have changed

				for(var i = 0; i < arguments.length; ++i) {
					var response = arguments[i];
					var film = response[0];
					var runtime = response[1];
					var error = response[2];
					if(response.length == 2) { // [film, runtime]
						film.length = runtime;
					} else if (response.length == 3) { // [film, runtime, error]
						console.warn("'{0}' has invalid runtime: '{1}' ({2})".format(film.title, runtime, error));
						film.length = 100;
					} else { // something terribly wrong
						console.error("Invalid arguments #" + i + ": " + arguments[i]);
						film.length = 100;
					}
				}
				return $.map(arguments, function(response) { return response[0]; });
			})
			.done(function(films) {
				$.each(films, function() {
					$('#films #films_' + this.edi + ' + label .runtime').text(this.length);
				});
			})
		;
	},
	getFilmLength: function cineworld_getFilmLength(film) {
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
	getPerformances: function cineworld_getPerformances() {
		console.groupStart("Cineworld Performances");
		twister.cineworld.rebuildPerformancesTable();
		$('#performances_listing > table td[id^=performances_]').each(twister.cineworld.updatePerformance);
	},
	rebuildPerformancesTable: function cineworld_rebuildPerformancesTable() {
		var html = '';
		html += '<table id="performances">';

		html += '<thead><tr>';
		html += '<th>Cinema</th>';
		html += $.map(twister.cineworld.films.selectedIds, function(edi) {
			var film = twister.cineworld.films.all[edi];
			var filmDisplay = film.title + "<br/>("  + film.length + " min)";
			var filmTitle = JSON.stringify(film, null, '  ').replace(/"/g,'&quot;');
			return '<th class="film" title="' + filmTitle + '">' + filmDisplay + '</th>';
		}).join('');
		html += '<th>Planner</th>';
		html += '</tr></thead>';

		html += '<tbody>';
		html += $.map(twister.cineworld.cinemas.selectedIds, function(cid) {
			var cinema = twister.cineworld.cinemas.all[cid];
			var cinemaDisplay = cinema.name + ' (<a href="http://maps.google.com/?q=' + cinema.postcode + '">' + cinema.postcode + '</a>)';
			var cinemaTitle = JSON.stringify(cinema, null, '  ').replace(/"/g,'&quot;');
			var row = '';
			row += '<tr>';
			row += '<td title="' + cinemaTitle + '">' + cinemaDisplay + '</td>';
			row += $.map(twister.cineworld.films.selectedIds, function(edi) {
						return '<td id="performances_'+cid+'_'+edi+'" cid="'+cid+'" edi="'+edi+'"><img src="/static/images/ajax-loader.gif" /></td>';
				}).join('');
			row += '<td><ul id="planner_'+cid+'"></ul></td>';
			row += '</tr>';
			return row;
		}).join('');
		html += '</tbody>';
		html += '</table>';
		$('#performances_listing').html(html);
	},
	updatePerformance: function cineworld_updatePerformance() {
		var elem = $(this);
		var request = {
			key: twister.cineworld.const.apiKey,
			cinema: elem.attr('cid'),
			film: elem.attr('edi'),
			date: twister.cineworld.getDate()
		};
		var perfs = twister.cineworld.performances = twister.cineworld.performances || {};
		perfs[request.date] = perfs[request.date] || {};
		perfs[request.date][request.cinema] = perfs[request.date][request.cinema] || {};
		var perf = perfs[request.date][request.cinema][request.film];
		if(perf && new Date().getTime() - perf.retrieved.getTime() < 10 * 60 * 1000) { // use cache
			twister.cineworld.parsePerformances(elem, request, {performances: perf});
		} else {
			$.ajax({
				url: 'http://www.cineworld.com/api/quickbook/performances',
				data: request,
				success: function(response, status, xhr) {
					$.extend(response.performances, {retrieved: new Date()});
					twister.cineworld.parsePerformances(elem, request, response);
				}
			});
		}
	},
	parsePerformances: function(elem, request, response) {
		/*
		{
		"performances":[{"time":"13:00","available":true,"type":"reg","ad":true,"subtitled":false,"ss":false,"booking_url":"http://www.cineworld.co.uk/booking?performance=976170&key=9qfgpF7B"},{"time":"15:30","available":true,"type":"reg","ad":true,"subtitled":false,"ss":false,"booking_url":"http://www.cineworld.co.uk/booking?performance=976178&key=9qfgpF7B"},{"time":"18:10","available":true,"type":"reg","ad":true,"subtitled":false,"ss":false,"booking_url":"http://www.cineworld.co.uk/booking?performance=976186&key=9qfgpF7B"},{"time":"20:40","available":true,"type":"reg","ad":true,"subtitled":false,"ss":false,"booking_url":"http://www.cineworld.co.uk/booking?performance=976194&key=9qfgpF7B"}],
		"legends":[{"code":"reg","description":"Regular"}]
		}
		*/
		if (response.errors) {
			console.error(JSON.stringify(response));
			elem.html(JSON.stringify(response, null, '  '));
		} else {
			twister.cineworld.performances[request.date][request.cinema][request.film] = response.performances;
			elem.html($.map(response.performances, function(perf) {
				return '<a href="' + perf.booking_url + '" title="' + JSON.stringify(perf, null, '  ').replace(/"/g, '&quot;') + '">'
						+ perf.time
						+ '</a>';
			}).join(", "));
		}
		twister.cineworld.plan();
	},
	plan: function() {
		// TODO remove absolute dependencies
		if($('#performances_listing > table td[id^=performances_] > img').length != 0) return;
		if($('#films_checklist li label > .runtime:contains(?)').length != 0) return;
		if(twister.cineworld.cinemas.retrieveFilmsDelay.isInProgress() || twister.cineworld.films.selectedIds.length == 0) return;
		if(twister.cineworld.films.getPerformancesDelay.isInProgress()) return;
		console.groupEnd(); // Cineworld Performances
		console.groupStart("The Plan (cinemas=" +  twister.cineworld.cinemas.selectedIds + ", films=" + twister.cineworld.films.selectedIds + ")");
		var date = twister.cineworld.getDate();
		$.each(twister.cineworld.cinemas.selectedIds, function(cinemaIndex, cid) {
			var cinema = twister.cineworld.cinemas.all[cid];
			$.each(twister.cineworld.films.selectedIds, function(filmIndex, edi) {
				var film = twister.cineworld.films.all[edi];
				var performances = twister.cineworld.performances[date][cid][edi];
				if (performances === undefined) return true;
				$.each(performances, function(performanceIndex, perf) {
					var time = moment(date + perf.time, 'YYYYMMDDHH:mm');
					var adLength = twister.cineworld.const.defaults.adLength;
					var plan = {
						scheduledTime: time,
						startTime: time.clone().add('minutes', adLength),
						endTime: time.clone().add('minutes', adLength + film.length),
						adLength: adLength
					};
					plan.movieRange = moment().range(plan.startTime, plan.endTime);
					plan.fullRange = moment().range(plan.scheduledTime, plan.endTime);

					perf.date = date;
					perf.cinema = cinema;
					perf.film = film;
					perf.plan = plan;
				});
			});
			twister.cineworld.planCinema(cinema, twister.cineworld.planner.graph = [{
				watched: ["travel"],
				performance: {
					plan: {
						endTime: moment(date, 'YYYYMMDDHH')
					},
					film: {
						edi: -1
					}
				},
				next: [],
				level: 0
			}]);
		});
		if (twister.config.debug) {
			var html = '<span style="font-family: monospace">JSON (twister.cineworld.performances):<br/>\
				<a href="javascript:void(0);" onClick="twister.cineworld.planner.formatter.CollapseAll()">collapse</a>\
				<a href="javascript:void(0);" onClick="twister.cineworld.planner.formatter.CollapseLevel(3)">2+</a>\
				<a href="javascript:void(0);" onClick="twister.cineworld.planner.formatter.CollapseLevel(4)">3+</a>\
				<a href="javascript:void(0);" onClick="twister.cineworld.planner.formatter.CollapseLevel(5)">4+</a>\
				<a href="javascript:void(0);" onClick="twister.cineworld.planner.formatter.CollapseLevel(6)">5+</a>\
				<a href="javascript:void(0);" onClick="twister.cineworld.planner.formatter.CollapseLevel(7)">6+</a>\
				<a href="javascript:void(0);" onClick="twister.cineworld.planner.formatter.CollapseLevel(8)">7+</a>\
				<a href="javascript:void(0);" onClick="twister.cineworld.planner.formatter.ExpandAll()">expand all</a>\
			</span>\
			<div style="width: 100%; height: 200px; overflow: scroll">\
			<div id="plan_jsonDebug"></div>\
			</div>';
			$('#planner_listing').html(html);
			twister.cineworld.planner.formatter = new QuickJSONFormatter({
				CanvasId: "plan_jsonDebug",
				SingleTab: "\t",
				TabSize: 1,
				IsCollapsible: true,
				ImgExpanded: 'http://bodurov.com/JsonFormatter/images/Expanded.gif',
				ImgCollapsed: 'http://bodurov.com/JsonFormatter/images/Collapsed.gif',
				OnError: function(e) {
					console.warn(e);
				}
			});
			twister.cineworld.planner.formatter.Process(JSON.stringify(twister.cineworld.performances));
		}
		console.groupEnd(); // The Plan
	},
	planCinema: function cineworld_planCinema(cinema, graph) {
		if(graph.level == 0) console.groupStart("Plan Cinema: " + cinema.name + " (" + cinema.id + ")");
		var date = twister.cineworld.getDate();
		$.each(graph, function(nodeIndex, node) {
			$.each(twister.cineworld.films.selectedIds, function(filmIndex, edi) {
				if( $.inArray(1 * edi, node.watched) == -1) { // didn't watch it already
					var performances = twister.cineworld.performances[date][cinema.id][edi];
					if (performances === undefined) return true;
					$.each(performances, function(performanceIndex, perf) {
						if(node.performance.plan.endTime < (perf.plan.startTime)) { // starts after the other finishes
							node.next.push({
								watched: node.watched.concat(perf.film.edi),
								performance: perf,
								next: [],
								prev: node,
								level: node.level + 1
							});
						}
					});
				}
			});
			if(node.next.length != 0) {
				twister.cineworld.planCinema(cinema, node.next);
			} else if (twister.cineworld.films.selectedIds.length + 1 == node.watched.length && node.watched.length != 1) {
				var results = [];
				while(node.prev) {
					results.unshift(node);
					if(node.prev.performance.film.edi != -1
							&& node.performance.plan.movieRange.start - node.prev.performance.plan.movieRange.end > 25 * 60 * 1000) {
						console.log("Ruled out: break between movies too big: " + 
							Math.floor((node.performance.plan.movieRange.start - node.prev.performance.plan.movieRange.end) / 60 / 1000));
						return;
					}
					node = node.prev;
				}
				var result = '';
				result += results[0].performance.plan.movieRange.start.format("HH:mm");
				result += '&nbsp;-&nbsp;';
				result += results[results.length - 1].performance.plan.movieRange.end.format("HH:mm");
				result += '<ul>';
				$.each(results, function() {
					if(this.prev.performance.film.edi != -1) {
						result += '\n\t<li>' + this.prev.performance.plan.movieRange.end.format("HH:mm")
								+ "-" +  this.performance.plan.movieRange.start.format("HH:mm")
								+ ": (" + Math.floor((this.performance.plan.movieRange.start - this.prev.performance.plan.movieRange.end) / 60 / 1000) + " minutes break)";
					}
					result += '\n\t<li>' + this.performance.plan.movieRange.start.format("HH:mm")
							+ "-" +  this.performance.plan.movieRange.end.format("HH:mm")
							+ ": " + this.performance.film.title;
				});
				result += '</ul>';
				console.log(result);
				$('#planner_' + cinema.id).append('<li>' + result + '</li>');
			} else if (node.watched.length > 1) {
				console.log("Ruled out: not all movies are included: " + node.watched);
			}
			if(graph.level == 0) console.groupEnd(); // Plan Cinema: cinema.name (cinema.id)
		});
	}
});
