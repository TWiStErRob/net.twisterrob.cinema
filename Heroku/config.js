var assert = require('assert');       // http://nodejs.org/api/assert.html
var extend = require('node.extend');

var config = require('./config.json');

// d:\Programming>heroku config --app twisterrob-cinema --shell
// NEO4J_URL=http://ab8c5ac27:8a79ffd54@02774ad3f.hosted.neo4j.org:7752
// PORT=8080
assert.notEqual(process.env.NEO4J_URL, undefined, "NEO4J_URL environment variable must be defined (=http://username:password@hostname:port).");
assert.notEqual(process.env.PORT, undefined, "PORT environment variable must be defined (=1234).");

extend(true, exports, config, {
	NEO4J_URL: process.env.NEO4J_URL,
	production: process.env.ENV === "prod",
	port: process.env.PORT
});
