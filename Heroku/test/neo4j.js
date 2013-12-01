var request = require('request');     // https://github.com/mikeal/request
var log = require('../logs').test;
var neo4j = require('../neo4j');

exports.testLifeCycle = {
	setUp: function (callback) {
		var test = this;
		test.class = 'Test';
		test.queryAll = 'START n = node(*) WHERE n.class = "' + test.class + '" RETURN n as nodeAlias';
		test.queryClean = 'START n = node(*) WHERE n.class = "' + test.class + '" DELETE n';
		test.nodeSelector = "nodeAlias";
		test.nodeID = function(result) { return result.data.namedID; };
		test.dataID = 'dataID';
		test.dataToNodeMapping = function(d) {
			d.namedID = d.dataID;
			delete d.dataID;
		};
		neo4j.init(function(err, graph) {
			if(err) { throw err; } // TODO test.ifError(err);
			test.graph = graph;
			callback();
		});
	},
	checkSetup: function(test) {
		test.data = this;
		test.ok(test.data.graph, "No graph");
		test.done();
	},
	tearDown : function(callback) {
		callback();
	},
	testInit: function(test) {
		test.data = this;
		this.graph.query(test.data.queryClean, function done(err, results) {
			test.ifError(err);
			test.done();
		});
	},
	testAdd: function(test) {
		test.data = this;
		var data = [
			{ dataID: 11, name: "Name 1" },
			{ dataID: 222, name: "Name 2" },
			{ dataID: 3333, name: "Name 3" }
		];
		neo4j.createNodes(test.data.graph, test.data.class, data, test.data.queryAll,
			test.data.nodeSelector,
			test.data.nodeID,
			test.data.dataID,
			test.data.dataToNodeMapping,
			/**
			 * <pre>
			 * createdNodes == [ {
			 *     id : '46',
			 *     data : {
			 *         namedID : 11,
			 *         _updated : '2013-11-29T12:53:58.328Z',
			 *         _created : '2013-11-29T12:53:58.328Z',
			 *         name : 'Name 1',
			 *         class : 'Test'
			 *     }
			 * }, {
			 *     id : '47',
			 *     data : {
			 *         namedID : 222,
			 *         _updated : '2013-11-29T12:53:58.328Z',
			 *         _created : '2013-11-29T12:53:58.328Z',
			 *         name : 'Name 2',
			 *         class : 'Test'
			 *     }
			 * }, {
			 *     id : '48',
			 *     data : {
			 *         namedID : 3333,
			 *         _updated : '2013-11-29T12:53:58.328Z',
			 *         _created : '2013-11-29T12:53:58.328Z',
			 *         name : 'Name 3',
			 *         class : 'Test'
			 *     }
			 * } ]
			 * </pre>
			 */
			function done(error, createdNodes, updatedNodes, deletedNodes) {
				test.ifError(error);
				test.equal(updatedNodes.length, 0);
				test.equal(deletedNodes.length, 0);
				test.ok(0 < createdNodes.length, "No nodes inserted.");
				test.equal(createdNodes.length, data.length);
				var firstCreated = createdNodes[0].data._created,
				    firstUpdated = createdNodes[0].data._updated;
				test.ok(firstCreated, "No creation date");
				test.ok(firstUpdated, "No update date");
				for(var i = 0, len = data.length; i < len; i++) {
					test.equal(createdNodes[i].data.namedID, data[i].dataID);
					test.equal(createdNodes[i].data.name, data[i].name);
					test.equal(createdNodes[i].data.class, test.data.class);
					test.equal(createdNodes[i].data._created, firstCreated);
					test.equal(createdNodes[i].data._updated, firstUpdated);
				}
				test.done();
			}
		);
	},
	testExisting: function(test) {
		test.data = this;
		test.data.graph.query(test.data.queryAll, function(err, results) {
			test.ifError(err);
			//console.log(results);
			test.done();
		});
	},
	testUpdate: function(test) {
		test.data = this;
		var data = [
			{ dataID: 11, name: "Name 1" },
			{ dataID: 222, name: "Name 222" },
			{ dataID: 3333, name: "Name 3333" }
		];
		neo4j.createNodes(test.data.graph, test.data.class, data, test.data.queryAll,
			test.data.nodeSelector,
			test.data.nodeID,
			test.data.dataID,
			function dataToNodeMapping(d) {
				test.fail("Should not be called");
			},
			function done(error, createdNodes, updatedNodes, deletedNodes) {
				test.ifError(error);
				test.equal(createdNodes.length, 0);
				test.equal(deletedNodes.length, 0);
				test.ok(0 < updatedNodes.length, "No nodes updated.");
				test.equal(updatedNodes.length, data.length);
				var firstCreated = updatedNodes[0].data._created,
				    firstUpdated = updatedNodes[0].data._updated;
				test.ok(firstCreated, "No creation date");
				test.ok(firstUpdated, "No update date");
				for(var i = 0, len = data.length; i < len; i++) {
					test.equal(updatedNodes[i].data.namedID, data[i].dataID);
					//test.equal(createdNodes[i].data.name, data[i].name);
					test.equal(updatedNodes[i].data.class, "Test");
					test.equal(updatedNodes[i].data._created, firstCreated);
					test.equal(updatedNodes[i].data._updated, firstUpdated);
				}
				test.done();
			}
		);
	}
};
