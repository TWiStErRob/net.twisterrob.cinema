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
			}, function (err, response, body) {
				if(err) throw err;
				_.each(body.films, function(film) {
					film.cineworldInternalID = film.id; delete film.id;
					film.poster_url = body.base_url + film.poster; delete film.poster;
					film.release = moment(film.release, 'YYYYMMDD').format();
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
		}
	}, function(err, results) {
		if(err) console.log(err);
//		var batch = graph.createBatch();
//		graph.query(batch, "MATCH f:Film, r:Response CREATE UNIQUE r-[:UDPATED]->f", function(err, node) {
//			
//		});
	});
});
