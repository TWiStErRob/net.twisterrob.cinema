var assert = require('assert');       // http://nodejs.org/api/assert.html
var fs = require('fs');               // http://nodejs.org/api/fs.html
var http = require('http');           // http://nodejs.org/api/http.html
var url = require('url');             // http://nodejs.org/api/url.html
var restify = require('restify');     // http://mcavage.me/node-restify / https://github.com/mcavage/node-restify
var extend = require('node.extend');  // https://github.com/dreamerslab/node.extend
var bunyan = require('bunyan');
var neo4j = require('neo4j-js');
var package = require('./package.json');
var config = require('./config.json');

//var bunyanProc = require('child_process').spawn('C:/Users/TWiStEr/AppData/Roaming/npm/bunyan.cmd');
var logs = {
	app: bunyan.createLogger({
		name: 'app',
		stream: process.stderr
	}),
	audit: bunyan.createLogger({
		name: 'audit',
		stream: process.stdout
	})
}, log = logs.app;

// d:\Programming>heroku config --app twisterrob-cinema --shell
// NEO4J_URL=http://ab8c5ac27:8a79ffd54@02774ad3f.hosted.neo4j.org:7752
// PORT=8080
assert.notEqual(process.env.NEO4J_URL, undefined, "NEO4J_URL environment variable must be defined (=http://username:password@hostname:port).");
assert.notEqual(process.env.PORT, undefined, "PORT environment variable must be defined (=1234).");

var neo4jURL = url.parse(process.env.NEO4J_URL, false /* queryStringAsObject */);
var neo4jOptions = {
	auth: neo4jURL.auth,
	host: neo4jURL.hostname,
	port: neo4jURL.port,
	path: neo4jURL.pathname + config.NEO4J_REST_PATH + config.NEO4J_CYPHER_PATH,
	method: 'POST',
	headers: {
		'Content-Type': 'application/json', // outgoing content type
		'Accept': 'application/json; charset=UTF-8', // expected content type
	}
};
log.info(neo4jOptions, "Using NEO4J rest URL: %s", process.env.NEO4J_URL);

var graph; // initialized in neo4j.connect
var queries = {}; // initialized in neo4j.connect
var neo4jConnection = process.env.NEO4J_URL + '/' + config.NEO4J_REST_PATH;
log.info('Connecting to: %s', neo4jConnection);
neo4j.connect(neo4jConnection, function (err, newGraph) {
	if (err) {
		throw err;
	}
	graph = newGraph;
	extend(queries, {
		getFilm: fs.readFileSync(__dirname + '/queries/getFilm.cypher', "utf8")
	});
	log.info('Connected to: %s', graph.version);
});
function getFilm(req, res, next) {
	graph.query(queries.getFilm, {
		node: parseInt(req.params.edi, 10),
	}, function (error, results) {
		res.send(results);
		next();
	});
}

function readHTML(fileName) {
	return function readHTML_internal(req, res, next) {
		fs.readFile(fileName, function (err, data) {
			if (err) {
				next(err);
			}
			res.setHeader('Content-Type', 'text/html');
			res.writeHead(200);
			res.end(data);
			next();
		});
	};
}

var server = restify.createServer({
	name: package.name + "-" + package.version,
	version: package.version,
	host: 'localhost'
});
server.use(restify.gzipResponse());
server.use(restify.queryParser({ mapParams: false }));
server.use(restify.bodyParser({ mapParams: false }));
server.use(restify.jsonp());
server.use(restify.acceptParser(server.acceptable));
var audit = restify.auditLogger({
	log: logs.audit
});
server.use(function(req, res, next) {
	audit(req, res, next);
	return next();
});
server.on('after', audit);

server.get(/\/static\/?.*/, restify.serveStatic({
	directory: './static' // FIXME change when updating to 2.6.1
}));
server.get('/', readHTML(__dirname + '/static/static/index.html'));
server.get('/film/:edi', getFilm);

server.listen(process.env.PORT, function() {
	log.info("%s listening at %s", server.name, server.url);
});
