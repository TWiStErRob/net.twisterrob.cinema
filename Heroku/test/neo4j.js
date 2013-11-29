var request = require('request');     // https://github.com/mikeal/request
var log = require('../logs').test;
var neo4j = require('../neo4j');

exports.testLifeCycle = {
	setUp: function (callback) {
		var test = this;
		test.class = 'Test';
		test.queryAll = 'START n = node(*) WHERE n.class = "' + test.class + '" RETURN n';
		test.queryClean = 'START n = node(*) WHERE n.class = "' + test.class + '" DELETE n';
		test.IDFuncNode = function(n) {
			return n.id;
		};
		test.IDFuncData = function(d) {
			return d.dataID;
		};
		test.dataToNodeMapping = function(d) {
			d.namedID = d.dataID;
			delete d.dataID;
		};
		neo4j.init(function(err, graph) {
			// TODO assert graph
//			test.ifError(err);
//			test.ok(graph, "No graph");
			test.graph = graph;
			callback();
		});
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
			test.data.IDFuncNode,
			test.data.IDFuncData,
			test.data.dataToNodeMapping,
			/**
			 * <pre>
			 * cinemaNodes == [ {
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
			function done(error, cinemaNodes) {
				test.ifError(error);
				test.ok(0 < cinemaNodes.length, "No nodes inserted.");
				test.equal(cinemaNodes.length, data.length);
				var firstCreated = cinemaNodes[0].data._created, firstUpdated = cinemaNodes[0].data._updated;
				test.ok(firstCreated, "No creation date");
				test.ok(firstUpdated, "No update date");
				for(var i = 0, len = data.length; i < len; i++) {
					test.equal(cinemaNodes[i].data.namedID, data[i].dataID);
					test.equal(cinemaNodes[i].data.name, data[i].name);
					test.equal(cinemaNodes[i].data.class, test.data.class);
					test.equal(cinemaNodes[i].data._created, firstCreated);
					test.equal(cinemaNodes[i].data._updated, firstUpdated);
				}
				test.done();
			}
		);
	},
	/*testUpdate: function(test) {
		test.data = this;
		var data = [
			{ dataID: 11, name: "Name 1" },
			{ dataID: 222, name: "Name 222" },
			{ dataID: 3333, name: "Name 3333" }
		];
		neo4j.createNodes(test.data.graph, test.data.class, data, test.data.queryAll,
			test.data.IDFuncNode,
			test.data.IDFuncData,
			function dataToNodeMapping(d) {
				test.fail("Should not be called");
			},
			function done(error, cinemaNodes) {
				test.ifError(error);
				test.ok(0 < cinemaNodes.length, "No nodes updated.");
				test.equal(cinemaNodes.length, data.length);
				var firstCreated = cinemaNodes[0].data._created, firstUpdated = cinemaNodes[0].data._updated;
				test.ok(firstCreated, "No creation date");
				test.ok(firstUpdated, "No update date");
				for(var i = 0, len = data.length; i < len; i++) {
					test.equal(cinemaNodes[i].data.namedID, data[i].dataID);
					//test.equal(cinemaNodes[i].data.name, data[i].name);
					test.equal(cinemaNodes[i].data.class, "Test");
					test.equal(cinemaNodes[i].data._created, firstCreated);
					test.equal(cinemaNodes[i].data._updated, firstUpdated);
				}
				test.done();
			}
		);
	}*/
};
