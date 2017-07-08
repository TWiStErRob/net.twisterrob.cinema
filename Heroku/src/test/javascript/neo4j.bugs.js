var neo4j = require('neo4j-js');      // https://github.com/bretcope/neo4j-js/blob/master/docs/Documentation.md

exports.testBugs = {
	setUp: function (callback) {
		var test = this;
		this.callback = function(test) {
			return function(err, results) {
				test.ifError(err);
				test.done();
			}
		}
		neo4j.connect('http://cinema:zGu1PfA3JWQySjRSV6AO@cinema.sb01.stations.graphenedb.com:24789/db/data/', function (err, graph) {
			if (err) { throw err; }
			console.log('Connected to ' + graph.version);
			test.graph = graph;
			callback();
		});
	},
	beforeClass: function(test) {
		test.ok(this.graph, "No graph");
		this.graph.query('MATCH (x:X) OPTIONAL MATCH (x)-[r]-(y:Y) DELETE x, r, y', this.callback(test));
	},
	testInit: function(test) {
		this.graph.query('CREATE (_0:X  {name:"a"}), (_1:X  {name:"b"}), (_2:Y  {name:"c"}), _0-[:REF]->_2', this.callback(test));
	},
	testWorks: function(test) { // both x and y are non-nulls in all rows
		// result row1: a, c;
		this.graph.query('MATCH (x:X) MATCH (x)-[:REF]->y RETURN x, y',
			function check(err, results) {
				test.ifError(err);
				console.log(JSON.stringify(results, true, '    '));
				test.equal(results.length, 1);
				test.equal(results[0].x.data.name, 'a');
				test.equal(results[0].y.data.name, 'c');
				test.done();
			}
		);
	},
	testBug1a: function(test) { // null first
		// result row1: b and null; row2: a and c
		this.graph.query('MATCH (x:X) OPTIONAL MATCH (x)-[:REF]->y RETURN x, y ORDER BY x.name DESC',
			function check(err, results) {
				test.ifError(err);
				console.log(JSON.stringify(results, true, '    '));
				test.equal(results.length, 2);
				test.equal(results[0].x.data.name, 'b');
				test.equal(results[0].y, null);
				test.equal(results[1].x.data.name, 'a');
				test.equal(results[1].y.data.name, 'c');
				test.ok(typeof results[1].x.self === 'undefined');
				test.ok(typeof results[1].y.self === 'string'); // should be undefined too
				test.done();
			}
		);
	},
	testBug1b: function(test) { // null first as empty object
		// result row1: b and {}; row2: a and c
		this.graph.query('MATCH (x:X) OPTIONAL MATCH (x)-[:REF]->y RETURN x, COALESCE(y, {}) as y ORDER BY x.name DESC',
			function check(err, results) {
				test.ifError(err);
				console.log(JSON.stringify(results, true, '    '));
				test.equal(results.length, 2);
				test.equal(results[0].x.data.name, 'b');
				test.deepEqual(results[0].y, {});
				test.equal(results[1].x.data.name, 'a');
				test.equal(results[1].y.data.name, 'c');
				test.ok(typeof results[1].x.self === 'undefined');
				test.ok(typeof results[1].y.self === 'string'); // should be undefined too
				test.done();
			}
		);
	},
	/*
	node_modules\neo4j-js\lib\Base.js:44
                this.id = neo4j.Utils.parseId(obj.self);
                                                 ^
	TypeError: Cannot read property 'self' of null
	    at Node.Base (node_modules\neo4j-js\lib\Base.js:44:36)
	    at new Node (node_modules\neo4j-js\lib\Node.js:42:14)
	    at query (node_modules\neo4j-js\lib\Graph.js:129:76)
	    at IncomingMessage.module.exports.connect.Request.createCallback (node_modules\neo4j-js\lib\Neo4jApi.js:262:6)
	    at IncomingMessage.EventEmitter.emit (events.js:126:20)
	    at IncomingMessage._emitEnd (http.js:366:10)
	    at HTTPParser.parserOnMessageComplete [as onMessageComplete] (http.js:149:23)
	    at Socket.socketOnData [as ondata] (http.js:1367:20)
	    at TCP.onread (net.js:403:27)
	*/
	testBug2a: function(test) { // null not first
		// result row1: a, c; row2: b, null
		this.graph.query('MATCH (x:X) OPTIONAL MATCH (x)-[:REF]->y RETURN x, y ORDER BY x.name ASC',
			function check(err, results) {
				// doesn't get here, throws on a different "thread"
				test.ifError(err);
				console.log(JSON.stringify(results, true, '    '));
				test.done();
			}
		);
	},
	/*
	node_modules\neo4j-js\lib\Neo4jUtils.js:129
                return _idRegex.exec(url)[0];
                                         ^
	TypeError: Cannot read property '0' of null
	    at module.exports.parseId (node_modules\neo4j-js\lib\Neo4jUtils.js:129:28)
	    at Node.Base (node_modules\neo4j-js\lib\Base.js:44:25)
	    at new Node (node_modules\neo4j-js\lib\Node.js:42:14)
	    at query (node_modules\neo4j-js\lib\Graph.js:129:76)
	    at IncomingMessage.module.exports.connect.Request.createCallback (node_modules\neo4j-js\lib\Neo4jApi.js:262:6)
	    at IncomingMessage.EventEmitter.emit (events.js:126:20)
	    at IncomingMessage._emitEnd (http.js:366:10)
	    at HTTPParser.parserOnMessageComplete [as onMessageComplete] (http.js:149:23)
	    at Socket.socketOnData [as ondata] (http.js:1367:20)
	    at TCP.onread (net.js:403:27)
	*/
	testBug2b: function(test) { // null not first as empty object
		// result row1: a, c; row2: b, {}
		this.graph.query('MATCH (x:X) OPTIONAL MATCH (x)-[:REF]->y RETURN x, COALESCE(y, {}) as y ORDER BY x.name ASC',
			function check(err, results) {
				// doesn't get here, throws on a different "thread"
				test.ifError(err);
				console.log(JSON.stringify(results, true, '    '));
				test.done();
			}
		);
	},

	afterClass: function(test) {
		test.ok(this.graph, "No graph");
		this.graph.query('MATCH (x:X) OPTIONAL MATCH (x)-[r]-(y:Y) DELETE x, r, y', this.callback(test));
	}
};
