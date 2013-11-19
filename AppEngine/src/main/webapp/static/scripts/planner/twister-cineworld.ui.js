"use strict";
twister.core.namespaces.create("twister.cineworld");
var NS = twister.core.namespaces.extend;

twister.cineworld = NS(twister.cineworld, {
	cinemas: {
		all: [ /* retrieved from cineworld API */ ],
		selectedIds: [ /* update when checklist checked */ ],
		retrieveFilmsDelay: new twister.utils.DelayedExecutor({ timeout: 300, name: "retrieveFilmsDelay" })
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
		retrievePerformancesDelay: new twister.utils.DelayedExecutor({ timeout: 300, name: "retrievePerformancesDelay" })
	},
	date: moment().add('days', 0).format("YYYY-MM-DD"),
	getDate: function() {
		return twister.cineworld.date.replace(/-/g, '');
	},
	dateChanged: function(e) {
		twister.cineworld.date = this.value;
		twister.cineworld.writeUI();
		twister.cineworld.cinemas.retrieveFilmsDelay.start();
	},
	rebuildUrl: function() {
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
		twister.cineworld.rebuildUrl();
		$("#dateDisplay").text(moment($("#date").val(), "YYYY-MM-DD").format("LL, dddd"));
	},
	getArgs: function() {
		var args = 'date=' + encodeURIComponent(twister.cineworld.date);
		args += '&' + $.map(twister.cineworld.cinemas.selectedIds, function(e) {return 'cinema=' + e; }).join('&');
		args += '&' + $.map(twister.cineworld.films.selectedIds, function(e) {return 'film=' + e; }).join('&');
		return args;
	},
	saveCinemas: function cineworld_saveCinemas(cinemas) {
		var cinemasMap = {};
		$.each(cinemas, function() {
			cinemasMap[this.id] = this;
		});
		twister.cineworld.cinemas.all = cinemasMap;
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
			callback: function retrieveFilmsCallback() {
				var filmsPromise = twister.cineworld.retrieveFilms();
				filmsPromise
					.then(twister.cineworld.getFilmLengths)
					.done(function(films) {
						$.each(films, function() {
							$('#films #films_' + this.edi + ' + label .runtime').text(this.length);
						});
					})
				;
				filmsPromise
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
		return twister.cineworld.getFilms(cinemaIds, date);
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

				twister.cineworld.films.retrievePerformancesDelay.start();
			}
		} );
		twister.cineworld.films.retrievePerformancesDelay.updateConfig({
			callback: function retrievePerformancesCallback() {
				twister.cineworld.rebuildPerformancesTable();
				var updatePerformanceRequests = [];
				$('#performances_listing > table td[id^=performances_]').each(function() {
					var promise = twister.cineworld.updatePerformance(this);
					updatePerformanceRequests.push(promise);
				});
				$.when.apply($, updatePerformanceRequests)
				.done(twister.cineworld.plan); // FIXME remove from here and add some kind of event
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
	updatePerformance: function(elem) {
		elem = $(elem);
		return twister.cineworld.getPerformance({
			cinemaId: elem.attr('cid'),
			filmEdi: elem.attr('edi')
		})
		.done(function(response) {
			elem.html($.map(response.performances, function(perf) {
				return '<a href="' + perf.booking_url + '" title="' + JSON.stringify(perf, null, '  ').replace(/"/g, '&quot;') + '">'
						+ perf.time
						+ '</a>';
			}).join(", "));
		})
		.fail(function (error) {
			elem.html(JSON.stringify(error, null, '  '));
		})
		;
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
	}
});
