"use strict";
twister.core.namespaces.create("twister");
twister.core.namespaces.create("twister.ui");
var NS = twister.core.namespaces.extend;

twister = NS(twister, {
	config: {
		debug: false,
		localUrlBase: '',
		logResponses: false,
		verboseMock: false
	}
});

twister.ui = NS(twister.ui, {
	screen: {
		layout: undefined,
		date: undefined
	},
	init: function() {
		twister.ui.initLayout();
		twister.ui.initCalendar();
	},
	initLayout: function() {
		twister.ui.screen.layout = $('body').layout({
			north: { // date
				initClosed: true
			},
			center: { // main_area
				childOptions: {
					west: { // cinemas
						size: 300,
						childOptions: {
							north: { // cinema controls
							},
							center: { // cinemas
							}
						}
					},
					center: {
						childOptions: {
							west: { // films
								size: 250
							},
							center: { // planner
							}
						}
					}
				}
			},
			south: { // placeholder
				initClosed: true
			}
		});
	},
	initCalendar: function() {
		moment.lang("en");
		$.datepicker.setDefaults($.datepicker.regional[""]);
		//timezoneJS.timezone.zoneFileBasePath = 'tz'; // needs --allow-file-access-from-files

		var config = {
			changeMonth: true,
			changeYear: true,
			showButtonPanel: true,
			showOtherMonths: true,
			selectOtherMonths: true,
			showWeek: true,
			firstDay: 1,
			numberOfMonths: 2,
			constrainInput: true,
			weekHeader: "",
			showOn: "both",
			dateFormat: "yy-mm-dd",
		};
		twister.ui.screen.date = $("#date")
			.datepicker($.extend({}, config, {
				onClose: function( selectedDate ) {
					//$( "#to" ).datepicker( "option", "minDate", selectedDate );
				}
			}))
			.change(twister.cineworld.dateChanged)
		;
	},
	showStatus: function(statusText) {
		$('#statusmessage').text(statusText).animate({'margin-bottom': 0}, 200);
		setTimeout(function() {
			$('#statusmessage').animate({'margin-bottom':-25}, 200);
		}, 2000);
	}
});

$.ajaxPrefilter(/*dataTypes, */ function global_ajaxPrefilter(options, originalOptions, jqXHR) {
	if(twister.config.logResponses) {
		jqXHR.done(function global_ajaxSuccess(data, textStatus, jqXHR) {
			console.groupCollapsed(options.url + (options.data ? '&' + $.param(options.data) : ''));
			console.log("Options: " + JSON.stringify(options));
			console.log("Data: " + JSON.stringify(data));
			console.groupEnd();
		});
	}
	jqXHR.fail(function global_ajaxError(jqXHR, textStatus, errorThrown) {
		console.error("{0}, {1}".format(textStatus, errorThrown));
	});
});

$(document).ready(function document_ready() {
	console.log("------------------------------- Document ready. ----------------------------");
	twister.ui.init();
	twister.cineworld.updateFromQuery();
	if(twister.config.debug) twister.cineworld.mock.init();
	twister.cineworld.writeUI();

	$.ajaxSetup({
		type: 'GET',
		traditional: true,
		dataType: 'jsonp'
	});

	var favs = twister.cineworld.getFavoriteCinemas();
	var cinemas = twister.cineworld.getCinemas()
		.done(twister.cineworld.saveCinemas)
		.done(function (cinemas) {
			twister.ui.showStatus("Got " + cinemas.length + " cinemas.");
		})
		.fail(function (responseWithErrors) {
			// response.errors is not empty.
			twister.ui.showStatus("Failure getting cinemas: " + JSON.stringify(response, null, '  '));
		})
		.done(twister.cineworld.displayCinemas)
	;
	$.when(cinemas, favs)
		.done(function cinemasWithFavoritesDone(cinemas, favoriteCinemaIds) {
			var preSelectIds = twister.cineworld.cinemas.selectedIds;
			var idsToSelect = preSelectIds.length ? preSelectIds : favoriteCinemaIds;
			if(idsToSelect.length != 0) {
				$('#cinemas li :checkbox').removeAttr('checked');
				$.each(idsToSelect, function select_cinema() {
					var cinemaElem = $('#cinemas li:has(:checkbox[value=' + this + '])');
					if(cinemaElem.length == 0) {
						console.warn("Cinema disappeared: " + this);
					} else {
						cinemaElem.find(':checkbox').attr('checked', 'checked');
						cinemaElem.find('.favorite').removeClass('grayscale');
					}
				});
				twister.cineworld.cinemasChanged();
			} else {
				$('#cinemas_checkLondon').click();
			}
		});
	;
});
