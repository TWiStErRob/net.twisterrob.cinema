var fs = require('fs');               // http://nodejs.org/api/fs.html
var extend = require('node.extend');  // https://github.com/dreamerslab/node.extend
var neo4j = require('neo4j-js');      // https://github.com/bretcope/neo4j-js/blob/master/docs/Documentation.md
                                      // https://github.com/bretcope/neo4j-js/blob/master/docs/REST.md
var _ = require('underscore');        // http://underscorejs.org/
var assert = require('assert');
var config = require('./config');

var log = require('./logs').app;

function toProperties(jsObj) {
	var json = JSON.stringify(jsObj);
	json = json.replace(/\\"/g,"\uFFFF");
	json = json.replace(/\"([^"]+)\":/g,"`$1`:");
	json = json.replace(/\uFFFF/g,"\\\"");
	return json;
}

// TODO use events api to emit graphloaded
module.exports = {
	init: function(callback) {
		if(global.graph !== undefined) {
			callback(undefined, global.graph);
			return;
		}
		var neo4jConnection = config.NEO4J_URL + '/' + config.NEO4J_REST_PATH;
		log.info('Neo4j connecting to: %s', neo4jConnection);
		neo4j.connect(neo4jConnection, function (err, graph) {
			if (err) {
				callback(err, undefined);
			}
			global.graph = graph;

			graph.queries = {
				endOfBatch: fs.readFileSync(__dirname + '/queries/endOfBatch.cypher', "utf8"),
				getFilm: fs.readFileSync(__dirname + '/queries/getFilm.cypher', "utf8"),
				getAllFilms: fs.readFileSync(__dirname + '/queries/getAllFilms.cypher', "utf8"),
				getAllCinemas: fs.readFileSync(__dirname + '/queries/getAllCinemas.cypher', "utf8"),
				getCinemasAuth: fs.readFileSync(__dirname + '/queries/getCinemasAuth.cypher', "utf8"),
				addView: fs.readFileSync(__dirname + '/queries/addView.cypher', "utf8"),
				addUser: fs.readFileSync(__dirname + '/queries/addUser.cypher', "utf8"),
				getUser: fs.readFileSync(__dirname + '/queries/getUser.cypher', "utf8"),
				addFavoriteCinema: fs.readFileSync(__dirname + '/queries/addFavoriteCinema.cypher', "utf8"),
				removeFavoriteCinema: fs.readFileSync(__dirname + '/queries/removeFavoriteCinema.cypher', "utf8"),
				getFavoriteCinemas: fs.readFileSync(__dirname + '/queries/getFavoriteCinemas.cypher', "utf8"),
			};

			var q = graph.query;
			graph.query = function queryWrapper(query, params) {
				log.debug({query: query, params: params}, query);
				return q.apply(this, Array.prototype.slice.call(arguments));
			};

			log.info('Neo4j connected to: %s', graph.version);
			callback(undefined, graph);
		});
	},
	createNodes: function(graph, clazz, data, queryAll, nodeProperty, nodeIDProperty, dataIDProperty, dataToNodeProperties, allDone) {
		graph.query(queryAll, function (error, results) {
			if(error) {
				allDone(error, [], [], []);
				return;
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
					if(true && createdNodes.length === newLength
							&& updatedNodes.length === existingLength
							&& deletedNodes.length === deletedLength) {
						log.info("Finished insering %d new and updating %d and deleting %d existing %ss for %s.",
								newLength, existingLength, deletedLength, clazz, now);
						allDone(undefined, createdNodes, updatedNodes, deletedNodes);
					}
				};
				if (newLength > 0) {
					var nodeInserter = function(error, result) {
						if(error) {
							throw error;
						}
						createdNodes.push(result[0].node);
						nodeFinished();
					};
					_.each(newContent, function(contentObj) {
						var dbObj = extend(true, {}, contentObj);
						dataToNodeProperties(dbObj);
						extend(dbObj, {
							class: clazz,
							_created: now
						});
						graph.query(batch, "CREATE (node:" + clazz + " " + toProperties(dbObj) + ") RETURN node", nodeInserter);
					});
				} else {
					log.info("No new %ss.", clazz);
				}
				var nodeUpdater = function(nodes, node) {
					return function(error, data) {
						if(error) {
							throw error;
						}
						node.data = data;
						nodes.push(node);
						nodeFinished();
					};
				};
				if(existingLength > 0) {
					_.each(existingContent, function(node) {
						assert.equal(node.data.class, clazz);
						var id = _.keys(_.indexBy([node], nodeIDProperty))[0];
						var newProperties = extend(true, {}, dataByID[id], {
							_updated: now
						});
						node.setProperties(batch, true, newProperties, nodeUpdater(updatedNodes, node));
					});
				} else {
					log.info("No updated %ss.", clazz);
				}
				if(deletedLength > 0) {
					_.each(deletedContent, function(node) {
						assert.equal(node.data.class, clazz);
						var id = _.keys(_.indexBy([node], nodeIDProperty))[0];
						var newProperties = extend(true, {}, dataByID[id], {
							_deleted: now
						});
						node.setProperties(batch, true, newProperties, nodeUpdater(deletedNodes, node));
					});
				} else {
					log.info("No deleted %ss.", clazz);
				}
				batch.run();
			} else {
				allDone(undefined, [], [], []);
			}
		});
	}
};
