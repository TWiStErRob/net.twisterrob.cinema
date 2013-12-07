var fs = require('fs');               // http://nodejs.org/api/fs.html
var http = require('http');           // http://nodejs.org/api/http.html
var url = require('url');             // http://nodejs.org/api/url.html
var express = require('express');     // http://expressjs.com/api.html
var extend = require('node.extend');  // https://github.com/dreamerslab/node.extend
var bunyan = require('bunyan');
var neo4j = require('neo4j-js');      // https://github.com/bretcope/neo4j-js/blob/master/docs/Documentation.md
                                      // https://github.com/bretcope/neo4j-js/blob/master/docs/REST.md
var auth = require('./auth');
var package = require('./package.json');
var config = require('./config');
var graph;
require('./neo4j').init(function(error, connected) {
	if(error) throw error;
	graph = connected;
});
var logs = require('./logs');
var log = logs.app;

function getFilm(req, res) {
	var edi = parseInt(req.param('edi'), 10);
	graph.query(graph.queries.getFilm, {
		filmEDI: edi
	}, function (error, results) {
		if(error) throw error;
		if(results.length) {
			res.jsonp(results[0].film);
		} else {
			res.send(404, 'Film with EDI #' + edi + ' is not found.');
		}
	});
}

function addView(req, res) {
	graph.query(graph.queries.addView, {
		filmEDI: parseInt(req.param('edi'), 10),
		cinemaID: parseInt(req.param('cinema'), 10),
		userID: 'twister'
	}, function (error, results) {
		if(error) throw error;
		res.jsonp(results);
	});
}

var app = express();
app.configure(function configure_all() {
	// global app.set
});
app.configure('development', function configure_dev() {
	app.set('app urlRoot', 'http://localhost:' + config.port + '/');
});
app.configure('production', function configure_prod() {
	app.set('app urlRoot', 'http://twisterrob-cinema.heroku.com/');
});

app.configure(function configure_use() {
	app.use(express.compress());
	app.use(express.static(__dirname + '/static/'));
	//app.use(express.logger());
	app.use(express.cookieParser());
	app.use(express.bodyParser());
	app.use(express.cookieSession({
		key: 'session',
		secret: 'twister'
	}));
});

auth.init(app);

app.get('/film/:edi', getFilm);
app.post('/film/:edi/view', addView);
app.listen(process.env.PORT, function() {
	log.info("Express listening on %d", process.env.PORT);
});

function ensureAuthenticated(req, res, next) {
	if (req.isAuthenticated()) { return next(); }
	res.redirect('/login');
}