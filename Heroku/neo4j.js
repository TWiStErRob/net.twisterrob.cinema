var fs = require('fs');               // http://nodejs.org/api/fs.html
var extend = require('node.extend');  // https://github.com/dreamerslab/node.extend
var neo4j = require('neo4j-js');      // https://github.com/bretcope/neo4j-js/blob/master/docs/Documentation.md
                                      // https://github.com/bretcope/neo4j-js/blob/master/docs/REST.md
var _ = require('underscore');        // http://underscorejs.org/
var config = require('./config');

var log = require('./logs').app;

var graphCache;

module.exports = {
	init: function(callback) {
		if(graphCache !== undefined) {
			callback(undefined, graphCache);
			return;
		}
		var neo4jConnection = config.NEO4J_URL + '/' + config.NEO4J_REST_PATH;
		log.info('Connecting to: %s', neo4jConnection);
		neo4j.connect(neo4jConnection, function (err, graph) {
			if (err) {
				throw err;
			}
			graphCache = graph;

			graph.queries = {
				endOfBatch: fs.readFileSync(__dirname + '/queries/endOfBatch.cypher', "utf8"),
				getFilm: fs.readFileSync(__dirname + '/queries/getFilm.cypher', "utf8"),
				getAllFilms: fs.readFileSync(__dirname + '/queries/getAllFilms.cypher', "utf8"),
				getAllCinemas: fs.readFileSync(__dirname + '/queries/getAllCinemas.cypher', "utf8"),
			};

			log.info('Connected to: %s', graph.version);
			callback(graph);
		});
	},
	createNodes: function(graph, clazz, bodyContents, existingQuery, getNodeID, getNewID, fixNewObj, done) {
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
};
