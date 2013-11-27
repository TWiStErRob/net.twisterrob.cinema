var request = require('request');     // https://github.com/mikeal/request
var log = require('../logs').test;
var neo4j = require('../neo4j');

exports.testInit = function(test) {
	neo4j.init(function(err, graph) {
		test.ifError(err);
		test.notEqual(graph, undefined, "No graph");
		test.notEqual(graph, null, "No graph");
		test.done();
	});
};

exports.testAdded = function(test) {
	neo4j.init(function(err, graph) {
		test.ifError(err);
		var data = [
			{ dataID: 11, name: "Name 1" },
			{ dataID: 222, name: "Name 2" },
			{ dataID: 3333, name: "Name 3" }
		];
		test.notEqual(graph, undefined, "No graph");
		test.notEqual(graph, null, "No graph");
		graph.query('START n = node(*) WHERE n.class = "Test" DELETE n', function(err, results) {
			test.ifError(err);
			neo4j.createNodes(graph, 'Test', data, 'START n = node(*) WHERE n.class = "Test" RETURN n',
				function(n) {
					return n.id;
				},
				function(d) {
					return d.dataID;
				},
				function(d) {
					d.namedID = d.dataID; // TODO make it non-invasive
					delete d.dataID;
				},
				function(error, cinemaNodes) {
					test.ifError(error);
					test.equal(cinemaNodes.length, 3);
					test.equal(cinemaNodes[0].data.namedID, data[0].namedID);
					test.equal(cinemaNodes[0].data.name, data[0].name);
					test.equal(cinemaNodes[0].data.class, "Test");
					test.done();
				}
			);
		});
	});
};
