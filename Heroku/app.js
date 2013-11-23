var assert = require('assert');       // http://nodejs.org/api/assert.html
var fs = require('fs');               // http://nodejs.org/api/fs.html
var http = require('http');           // http://nodejs.org/api/http.html
var url = require('url');             // http://nodejs.org/api/url.html
var restify = require('restify');     // http://mcavage.me/node-restify
var extend = require('node.extend');  // https://github.com/dreamerslab/node.extend
var package = require('./package.json');
var config = require('./config.json');

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

function getFilm(req, res, next) {
	console.log("Incoming request: ", req.url);
	var neoReq = http.request(neo4jOptions, function(neoRes) {
		console.log('STATUS: ' + neoRes.statusCode + ', HEADERS: ' + JSON.stringify(neoRes.headers));
		neoRes.setEncoding('utf8');
		var neoResponseData = "";
		neoRes.on('data', function (chunk) {
			neoResponseData += chunk;
		});
		neoRes.on('end', function() {
			console.log(neoResponseData);
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
		});
	});
	neoReq.on('error', function(e) {
		console.error(e);
		res.send(500, e);
	});
	neoReq.write(JSON.stringify({
		query: fs.readFileSync('queries/getFilm.cypher', "utf8"),
		params: {
			node: parseInt(req.params.edi, 10),
		}
	}));
	console.log("Calling Neo4j");
	neoReq.end();
}

var server = restify.createServer({
	name: package.name + "-" + package.version,
	version: package.version,
	host: 'localhost'
});
server.get('/film/:edi', getFilm);

server.listen(process.env.PORT, function() {
	console.log('%s listening at %s', server.name, server.url);
});
