var assert = require('assert');       // http://nodejs.org/api/assert.html
var extend = require('node.extend');

var config = require('../../../config/default-env.json');

// d:\Programming>heroku config --app twisterrob-cinema --shell
// NEO4J_URL=http://cinema-dev:QSMIvHhWJLKLbrE6B0IK@cinemadev.sb01.stations.graphenedb.com:24789
// NEO4J_URL=http://cinema:qUUHhFOpVbooJqIVxcb2@cinema.sb01.stations.graphenedb.com:24789
// PORT=8080
assert.notEqual(process.env.NEO4J_URL, undefined, "NEO4J_URL environment variable must be defined (=http://username:password@hostname:port).");
assert.notEqual(process.env.PORT, undefined, "PORT environment variable must be defined (=1234).");

extend(true, exports, config, {
	NEO4J_URL: process.env.NEO4J_URL,
	env: process.env.NODE_ENV,
	port: process.env.PORT
});
