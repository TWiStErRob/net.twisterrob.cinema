var fs = require('fs');               // http://nodejs.org/api/fs.html
var extend = require('node.extend');  // https://github.com/dreamerslab/node.extend
var neo4j = require('neo4j-js');      // https://github.com/bretcope/neo4j-js/blob/master/docs/Documentation.md
                                      // https://github.com/bretcope/neo4j-js/blob/master/docs/REST.md
var config = require('./config');

var log = require('./logs').app;

var graph;
module.exports = {
	init: function(callback) {
		if(graph !== undefined) {
			callback(undefined, graph);
			return;
		}
		var neo4jConnection = config.NEO4J_URL + '/' + config.NEO4J_REST_PATH;
		log.info('Connecting to: %s', neo4jConnection);
		neo4j.connect(neo4jConnection, function (err, newGraph) {
			if (err) {
				throw err;
			}
			graph = newGraph;

			graph.queries = {
				endOfBatch: fs.readFileSync(__dirname + '/queries/endOfBatch.cypher', "utf8"),
				getFilm: fs.readFileSync(__dirname + '/queries/getFilm.cypher', "utf8"),
				getAllFilms: fs.readFileSync(__dirname + '/queries/getAllFilms.cypher', "utf8"),
				getAllCinemas: fs.readFileSync(__dirname + '/queries/getAllCinemas.cypher', "utf8"),
			};

			log.info('Connected to: %s', newGraph.version);
			callback(graph);
		});
	}
};
