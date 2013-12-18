var request = require('request');     // https://github.com/mikeal/request
var log = require('./logs').task;
var neo4j = require('./neo4j');
var _ = require('underscore');
var moment = require('moment');
var async = require('async');

neo4j.init(function(err, graph) {
	if(err) throw err;
	async.series({
		public: function(callback) {
			request.get({
				uri: 'http://www.cineworld.com/api/quickbook/films',
				json: true,
				qs: {
					key: "9qfgpF7B",
					full: true
				}
				/*
				3D: false
				classification: "15"
				edi: 63829
				film_url: "http://www.cineworld.co.uk/whatson/6237"
				id: 6237
				imax: false
				poster_url: "http://www.cineworld.co.uk/assets/media/films/6237_poster.jpg"
				still_url: "http://www.cineworld.co.uk/assets/media/films/6237_still.jpg"
				title: "This Is The End"
				 */
			}, function (err, response, body) {
				if(err) throw err;
				_.each(body.films, function(film) {
					film.cineworldID = film.id; delete film.id;
				});
				neo4j.createNodes(graph, 'Film', body.films, graph.queries.getAllFilms,
					"film",
					function(filmNode) {
						return filmNode.data.edi;
					},
					"edi",
					function(error, createdNodes, updatedNodes, deletedNodes) {
						callback(error, {
							response: body,
							created: createdNodes, updated: updatedNodes, deleted: deletedNodes
						});
					}
				);
			});
		},
		internal: function(callback) {
			request.get({
				uri: 'http://www.cineworld.com/api/film/list',
				json: true,
				qs: {
					key: "9qfgpF7B",
					full: true
				}
				/*
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
			}, function (err, response, body) {
				if(err) throw err;
				_.each(body.films, function(film) {
					film.cineworldInternalID = film.id; delete film.id;
					film.poster_url = body.base_url + film.poster; delete film.poster;
					film.release = moment(film.release, 'YYYYMMDD').format();
					film.runtime = film.length; delete film.length;
				});
				neo4j.createNodes(graph, 'Film', body.films, graph.queries.getAllFilms,
					"film",
					function(filmNode) {
						return filmNode.data.edi;
					},
					"edi",
					function(error, createdNodes, updatedNodes, deletedNodes) {
						callback(error, {
							response: body,
							created: createdNodes, updated: updatedNodes, deleted: deletedNodes
						});
					}
				);
			});
		},
		detail: function(callback) {
			// TODO http://www.cineworld.co.uk/api/film/detail?key=9qfgpF7B&film=6237
		}
	}, function(err, results) {
		if(err) console.log(err);
//		var batch = graph.createBatch();
//		graph.query(batch, "MATCH f:Film, r:Response CREATE UNIQUE r-[:UDPATED]->f", function(err, node) {
//			
//		});
	});
});
