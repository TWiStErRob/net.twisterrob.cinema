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
				callback(err, undefined);
			}
			graphCache = graph;

			graph.queries = {
				endOfBatch: fs.readFileSync(__dirname + '/queries/endOfBatch.cypher', "utf8"),
				getFilm: fs.readFileSync(__dirname + '/queries/getFilm.cypher', "utf8"),
				getAllFilms: fs.readFileSync(__dirname + '/queries/getAllFilms.cypher', "utf8"),
				getAllCinemas: fs.readFileSync(__dirname + '/queries/getAllCinemas.cypher', "utf8"),
			};

			log.info('Connected to: %s', graph.version);
			callback(undefined, graph);
		});
	},
	createNodes: function(graph, clazz, bodyContents, existingQuery, getNodeID, getNewID, fixNewObj, done) {
		graph.query(existingQuery, function (error, results) {
			if(error) {
				throw error;
			}
			var dbIDs = _.map(results, getNodeID);
			var bodyIDs = _.map(bodyContents, getNewID);
			var existingContent = _.filter(bodyContents, function(contentObj) {
				return _.contains(dbIDs, getNewID(contentObj));
			}), existingLength = existingContent.length;
			var newContent = _.reject(bodyContents, function(contentObj) {
				return _.contains(dbIDs, getNewID(contentObj));
			}), newLength = newContent.length;
			var deletedContent = _.reject(results, function(dbNode) {
				return _.contains(bodyIDs, getNodeID(dbNode));
			}), deletedLength = deletedContent.length;

			if(newLength > 0 || existingLength > 0 || deletedLength > 0) {
				var batch = graph.createBatch();
				var now = new Date();
				log.info("Inserting %d new and updating %d and deleting %d existing %ss for %s.",
						newLength, existingLength, deletedLength, clazz, now);
				if (newLength > 0) {
					var nodes = [];
					var nodeInserter = function(error, node) {
						if(error) {
							throw error;
						}
						nodes.push(node);
						if(nodes.length === newLength) {
							log.info("Finished inserting %d new %ss.", newLength, clazz);
							done(error, nodes);
						}
					};
					for(var i = 0; i < newLength; i++) {
						var contentObj = newContent[i];
						contentObj.class = clazz;
						contentObj._created = now;
						contentObj._updated = now;
						fixNewObj(contentObj);
						graph.createNode(batch, contentObj, nodeInserter);
					}
				} else {
					log.info("No new %ss.", clazz);
				}
				if(existingLength > 0) {
					for(var i = 0; i < existingLength; i++) {
						var contentObj = existingContent[i];
						contentObj.class = clazz;
						contentObj._updated = now;
						// contentObj.update
					}
				} else {
					log.info("No updated %ss.", clazz);
				}
				if(deletedLength > 0) {
					for(var i = 0; i < deletedLength; i++) {
						var contentObj = deletedContent[i];
						contentObj.class = clazz;
						contentObj._deleted = now;
						// contentObj.update
					}
				} else {
					log.info("No deleted %ss.", clazz);
				}
				batch.run();
			}
		});
	}
};
