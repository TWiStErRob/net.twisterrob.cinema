var request = require('request');     // https://github.com/mikeal/request
var _ = require('underscore');
var extend = require('node.extend');  // https://github.com/dreamerslab/node.extend
var log = require('./logs').task;
var config = require('./config');

var neo4j = require('./neo4j');

function createNodes(graph, clazz, bodyContents, existingQuery, getNodeID, getNewID, fixNewObj, done) {
	graph.query(existingQuery, function (error, results) {
		if(error) {
			throw error;
		}
		var ids = _.map(results, getNodeID);
		var films = _.reject(bodyContents, function(film) {
			return _.contains(ids, getNewID(film));
		});
		if (films.length > 0) {
			log.info("Inserting %d new %ss.", films.length, clazz);
			var nodes = [];
			var batch = graph.createBatch();
			var nodeInserter = function(error, node) {
				if(error) {
					throw error;
				}
				nodes.push(node);
				if(nodes.length === films.length) {
					log.info("Finished inserting %d new %ss.", films.length, clazz);
					done(error, nodes);
				}
			};
			for(var i = 0, len = films.length; i < len; i++) {
				var film = films[i];
				film.class = clazz;
				fixNewObj(film);
				graph.createNode(batch, film, nodeInserter);
			}
			batch.run();
		} else {
			log.info("No new %ss.", clazz);
		}
	});
}

function updateCinemas(graph) {
	request.get({
		uri: 'http://www.cineworld.com/api/quickbook/cinemas',
		json: true,
		qs: {
			key: "9qfgpF7B",
			full: true
		}
	}, function (error, response, body) {
		if(error) {
			throw error;
		}
		createNodes(graph, 'Cinema', body.cinemas, graph.queries.getAllCinemas,
			function(cinemaNode) {
				return cinemaNode.id;
			},
			function(cinema) {
				return cinema.cineworldID;
			},
			function(cinemaToInsert) {
				cinemaToInsert.cineworldID = cinemaToInsert.id;
				delete cinemaToInsert.id;
			},
			function(error, cinemaNodes) {
				//console.log(cinemaNodes.length);
			}
		);
	});
}

function updateFilms(graph) {
	request.get({
		uri: 'http://www.cineworld.com/api/quickbook/films',
		json: true,
		qs: {
			key: "9qfgpF7B",
			full: true
		}
	}, function (error, response, body) {
		if(error) {
			throw error;
		}
		createNodes(graph, 'Film', body.films, graph.queries.getAllFilms,
			function(filmNode) {
				return filmNode.film.data.edi;
			},
			function(film) {
				return film.edi;
			},
			function(filmToInsert) {
				filmToInsert.cineworldID = filmToInsert.id;
				delete filmToInsert.id;
			},
			function(error, filmNodes) {
				//console.log(filmNodes.length);
			}
		);
	});
}

neo4j.init(function(graph) {
	updateCinemas(graph);
	updateFilms(graph);
});
