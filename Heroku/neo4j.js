var fs = require('fs');               // http://nodejs.org/api/fs.html
var extend = require('node.extend');  // https://github.com/dreamerslab/node.extend
var neo4j = require('neo4j-js');      // https://github.com/bretcope/neo4j-js/blob/master/docs/Documentation.md
                                      // https://github.com/bretcope/neo4j-js/blob/master/docs/REST.md
var _ = require('underscore');        // http://underscorejs.org/
var assert = require('assert');
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
	createNodes: function(graph, clazz, data, queryAll, nodeProperty, nodeIDProperty, dataIDProperty, dataToNodeProperties, allDone) {
		graph.query(queryAll, function (error, results) {
			if(error) {
				throw error;
			}
			var nodes = _.pluck(results, nodeProperty);
			var nodesByID = _.indexBy(nodes, nodeIDProperty),
			    nodeIDs = _.keys(nodesByID);
			var dataByID = _.indexBy(data, dataIDProperty),
			    dataIDs = _.keys(dataByID);
			var newContent = _.omit(_.clone(dataByID), nodeIDs),
			    newLength = _.size(newContent);
			var existingContent = _.pick(_.clone(nodesByID), dataIDs),
			    existingLength = _.size(existingContent);
			var deletedContent = _.omit(_.clone(nodesByID), dataIDs),
			    deletedLength = _.size(deletedContent);

			if(newLength > 0 || existingLength > 0 || deletedLength > 0) {
				var batch = graph.createBatch();
				var now = new Date();
				log.info("Inserting %d new and updating %d and deleting %d existing %ss for %s.",
						newLength, existingLength, deletedLength, clazz, now);
				var createdNodes = [], updatedNodes = [], deletedNodes = [];
				var nodeFinished = function() {
					if(createdNodes.length === newLength && updatedNodes.length === existingLength) {
						log.info("Finished insering %d new and updating %d and deleting %d existing %ss for %s.",
								newLength, existingLength, deletedLength, clazz, now);
						allDone(error, createdNodes, updatedNodes, deletedNodes);
					}
				};
				if (newLength > 0) {
					var nodeInserter = function(error, node) {
						if(error) {
							throw error;
						}
						createdNodes.push(node);
						nodeFinished();
					};
					_.each(newContent, function(contentObj) {
						var dbObj = extend(true, {}, contentObj);
						dataToNodeProperties(dbObj);
						extend(dbObj, {
							class: clazz,
							_created: now,
							_updated: now
						});
						graph.createNode(batch, dbObj, nodeInserter);
					});
				} else {
					log.info("No new %ss.", clazz);
				}
				if(existingLength > 0) {
					//console.log(existingContent);
					_.each(existingContent, function(node) {
						//console.log(node);
						assert.equal(node.data.class, clazz);
						node.data._updated = now;
						// contentObj.update
						updatedNodes.push(node);
						nodeFinished();
					});
				} else {
					log.info("No updated %ss.", clazz);
				}
				if(deletedLength > 0) {
					console.log(deletedContent);
					_.each(deletedContent, function(contentObj) {
						assert.equal(contentObj.class, clazz);
						contentObj._deleted = now;
						// contentObj.update
					});
				} else {
					log.info("No deleted %ss.", clazz);
				}
				batch.run();
			}
		});
	}
};
