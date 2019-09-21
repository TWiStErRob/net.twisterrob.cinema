var assert = require('assert');       // http://nodejs.org/api/assert.html
var extend = require('node.extend');

var config = require('../../../config/default-env.json');

assert.notStrictEqual(process.env.NEO4J_URL, undefined, "NEO4J_URL environment variable must be defined (=http://username:password@hostname:port).");
assert.notStrictEqual(process.env.PORT, undefined, "PORT environment variable must be defined (=1234).");

extend(true, exports, config, {
	NEO4J_URL: process.env.NEO4J_URL,
	env: process.env.NODE_ENV,
	port: process.env.PORT
});
