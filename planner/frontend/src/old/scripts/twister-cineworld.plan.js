"use strict";
twister.core.namespaces.create("twister.cineworld");
var NS = twister.core.namespaces.extend;

twister.cineworld = NS(twister.cineworld, {
	consts: {
		defaults: {
			adLength: 15,
			breakLength: 5
		}
	},
	planner: {
		// http://stackoverflow.com/a/14972456/253468
	},
	plan: function() {
		// TODO remove absolute dependencies (i.e. call plan() only when we have everything), and let other things happen when it's done
		if($('#performances_listing > table td[id^=performances_] > img').length != 0) {
			console.error("Can't plan(): not all performances are loaded"); return;
		}
		if($('#films_checklist li label > .runtime:contains(?)').length != 0) {
			console.error("Can't plan(): not all runtimes are loaded"); return;
		}
		if(twister.cineworld.cinemas.retrieveFilmsDelay.isInProgress() || twister.cineworld.films.selectedIds.length == 0) {
			console.error("Can't plan(): film retrieval in progress or no films"); return;
		}
		if(twister.cineworld.films.retrievePerformancesDelay.isInProgress())  {
			console.error("Can't plan(): performance retrieval in progress"); return;
		}
		console.groupStart("The Plan (cinemas=" +  twister.cineworld.cinemas.selectedIds + ", films=" + twister.cineworld.films.selectedIds + ")");
		var date = twister.cineworld.getDate();
		$.each(twister.cineworld.cinemas.selectedIds, function(cinemaIndex, cid) {
			var cinema = twister.cineworld.cinemas.all[cid];
			$.each(twister.cineworld.films.selectedIds, function(filmIndex, edi) {
				var film = twister.cineworld.films.all[edi];
				var performances = twister.cineworld.performances[date][cid][edi].performances;
				if (performances === undefined) return true;
				$.each(performances, function(performanceIndex, perf) {
					var time = moment(date + perf.time, 'YYYYMMDDHH:mm');
					var adLength = twister.cineworld.consts.defaults.adLength;
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
	planCinema: function(cinema, graph) {
		if(graph.level == 0) console.groupStart("Plan Cinema: " + cinema.name + " (" + cinema.id + ")");
		var date = twister.cineworld.getDate();
		$.each(graph, function(nodeIndex, node) {
			$.each(twister.cineworld.films.selectedIds, function(filmIndex, edi) {
				if( $.inArray(1 * edi, node.watched) == -1) { // didn't watch it already
					var performances = twister.cineworld.performances[date][cinema.id][edi].performances;
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
				var first = results[0].performance.plan.movieRange.start;
				var last = results[results.length - 1].performance.plan.movieRange.end;
				var workEnd = moment(first).startOf('day').add(moment.duration({hours:17, minutes:30}));
				var result = '';
				result += first.format("HH:mm");
				result += '&nbsp;-&nbsp;';
				result += last.format("HH:mm");
				if(first.isBefore(workEnd)) {
					console.log("Ruled out: too early");
					// $('#planner_' + cinema.id).append('<li>' + result + ": too early" + '</li>');
					return;
				}
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
				//console.debug(result);
				$('#planner_' + cinema.id).append('<li>' + result + '</li>');
			} else if (node.watched.length > 1) {
				console.log("Ruled out: not all movies are included: " + node.watched);
			}
			if(graph.level == 0) console.groupEnd(); // Plan Cinema: cinema.name (cinema.id)
		});
	}
});
