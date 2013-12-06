var fs = require('fs');               // http://nodejs.org/api/fs.html
var http = require('http');           // http://nodejs.org/api/http.html
var url = require('url');             // http://nodejs.org/api/url.html
var restify = require('restify');     // http://mcavage.me/node-restify / https://github.com/mcavage/node-restify
var extend = require('node.extend');  // https://github.com/dreamerslab/node.extend
var bunyan = require('bunyan');
var neo4j = require('neo4j-js');      // https://github.com/bretcope/neo4j-js/blob/master/docs/Documentation.md
                                      // https://github.com/bretcope/neo4j-js/blob/master/docs/REST.md
var package = require('./package.json');
var config = require('./config');
var graph;
require('./neo4j').init(function(error, connected) {
	if(error) throw error;
	graph = connected;
});
var logs = require('./logs');
var log = logs.app;

function getFilm(req, res, next) {
	graph.query(graph.queries.getFilm, {
		node: parseInt(req.params.edi, 10)
	}, function (error, results) {
		if(error) throw error;
		res.send(results);
		next();
	});
}

function addView(req, res, next) {
	graph.query(graph.queries.addView, {
		filmEDI: parseInt(req.params.edi, 10),
		cinemaID: parseInt(req.body.cinema, 10)
	}, function (error, results) {
		if(error) {
			console.error(error);
			throw error;
		}
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
server.post('/film/:edi/view', addView);

server.listen(process.env.PORT, function() {
	log.info("%s listening at %s", server.name, server.url);
});
