var assert = require('assert');       // http://nodejs.org/api/assert.html
var fs = require('fs');               // http://nodejs.org/api/fs.html
var http = require('http');           // http://nodejs.org/api/http.html
var url = require('url');             // http://nodejs.org/api/url.html
var restify = require('restify');     // http://mcavage.me/node-restify / https://github.com/mcavage/node-restify
var extend = require('node.extend');  // https://github.com/dreamerslab/node.extend
var bunyan = require('bunyan');
var package = require('./package.json');
var config = require('./config.json');

var log = bunyan.createLogger({
	name: 'app',
	stream: process.stdout
})

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

function getFilm(req, res, next) {
	log.info("Incoming request: %s", req.url);
	var neoReq = http.request(neo4jOptions, function(neoRes) {
		log.info('STATUS: %s, HEADERS: %s', neoRes.statusCode, JSON.stringify(neoRes.headers));
		neoRes.setEncoding('utf8');
		var neoResponseData = "";
		neoRes.on('data', function (chunk) {
			neoResponseData += chunk;
		});
		neoRes.on('end', function() {
			log.debug(neoResponseData);
			var data = JSON.parse(neoResponseData);
			var out = [];
			for(var index = 0, dataLen = data.data.length; index < dataLen; ++index) {
				var outObj = {};
				for(var col = 0, colLen = data.columns.length; col < colLen; ++col) {
					var column = data.columns[col];
					var value = data.data[index][col];
					outObj[column] = value;
				}
				out.push(outObj);
			}
			res.send(out);
			next();
		});
	});
	neoReq.on('error', function(e) {
		log.error(e);
		res.send(500, e);
		next(e);
	});
	neoReq.write(JSON.stringify({
		query: fs.readFileSync(__dirname + '/queries/getFilm.cypher', "utf8"),
		params: {
			node: parseInt(req.params.edi, 10),
		}
	}));
	log.info("Calling Neo4j");
	neoReq.end();
}

function readHTML(fileName) {
	return function readHTML_internal(req, res, next) {
		fs.readFile(fileName, function (err, data) {
			if (err) {
				return next(err);
			}
			res.setHeader('Content-Type', 'text/html');
			res.writeHead(200);
			res.end(data);
			return next();
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
server.on('after', restify.auditLogger({
	log: bunyan.createLogger({
		name: 'audit',
		stream: process.stdout
	})
}));

server.get(/\/static\/?.*/, restify.serveStatic({
	directory: './static' // FIXME change when updating to 2.6.1
}));
server.get('/', readHTML(__dirname + '/static/static/index.html'));
server.get('/film/:edi', getFilm);

server.listen(process.env.PORT, function() {
	log.info("%s listening at %s", server.name, server.url);
});
