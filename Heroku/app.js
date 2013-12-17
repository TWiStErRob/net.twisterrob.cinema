var fs = require('fs');               // http://nodejs.org/api/fs.html
var http = require('http');           // http://nodejs.org/api/http.html
var url = require('url');             // http://nodejs.org/api/url.html
var express = require('express');     // http://expressjs.com/api.html
var extend = require('node.extend');  // https://github.com/dreamerslab/node.extend
var _ = require('underscore');        // http://underscorejs.org/
var request = require('request');     // https://github.com/mikeal/request
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
	listen();
});
var logs = require('./logs');
var log = logs.app;

function getFilm(req, res) {
	var params = {
		filmEDI: parseInt(req.param('edi'), 10)
	};
	graph.query(graph.queries.getFilm, params, function (error, results) {
		if(error) throw error;
		if(results.length) {
			res.jsonp(results[0].film);
		} else {
			res.send(404, 'Film with EDI #' + params.filmEDI + ' is not found.');
		}
	});
}

function addView(req, res) {
	var params = {
		filmEDI: parseInt(req.param('edi'), 10),
		cinemaID: parseInt(req.param('cinema'), 10),
		userID: req.user.id
	};
	graph.query(graph.queries.addView, params, function (error, results) {
		if(error) throw error;
		if(results.length !== 1) {
			res.send(404, 'No or more results found: ' + results.length);
		} else {
			res.jsonp({
				film: results[0].film.data,
				cinema: results[0].cinema.data,
				user: results[0].user.data,
				view: results[0].view.data
			});
		}
	});
}

function favCinema(req, res) {
	var params = {
		cinemaID: parseInt(req.param('cinema'), 10),
		userID: req.user.id
	};
	graph.query(graph.queries.addFavoriteCinema, params, function (error, results) {
		if(error) throw error;
		if(results.length !== 1) {
			res.send(404, 'No or more results found: ' + results.length);
		} else {
			res.jsonp(results[0].cinema.data);
		}
	});
}
function unFavCinema(req, res) {
	var params = {
			cinemaID: parseInt(req.param('cinema'), 10),
			userID: req.user.id
	};
	graph.query(graph.queries.removeFavoriteCinema, params, function (error, results) {
		if(error) throw error;
		if(results.length !== 1) {
			res.send(404, 'No or more results found: ' + results.length);
		} else {
			res.jsonp(results[0].cinema.data);
		}
	});
}

function getFavCinemas(req, res) {
	var params = {
		userID: req.user.id
	};
	graph.query(graph.queries.getFavoriteCinemas, params, function (error, results) {
		if(error) throw error;
		var data = [];
		for(var i = 0, len = results.length; i < len; ++i) {
			data.push(results[i].cinema.data);
		}
		res.jsonp(data);
	});
}

function getCinemas(req, res) {
	if (req.isAuthenticated()) {
		var params = {
			userID: req.user.id
		};
		graph.query(graph.queries.getCinemasAuth, params, function (error, results) {
			if(error) throw error;
			var data = [];
			for(var i = 0, len = results.length; i < len; ++i) {
				var result = results[i];
				var c = _.clone(result.cinema.data);
				c.fav = result.fav;
				data.push(c);
			}
			res.jsonp(data);
		});
	} else {
		graph.query(graph.queries.getAllCinemas, {}, function (error, results) {
			if(error) throw error;
			var data = [];
			for(var i = 0, len = results.length; i < len; ++i) {
				var result = results[i];
				var c = result.cinema.data;
				data.push(c);
			}
			res.jsonp(data);
		});
	}
}

function getFilms(req, res) {
	var cineParams = {
		key: "9qfgpF7B",
		date: req.param('date'),
		cinema: req.param('cinemaIDs')
	};
	if(cineParams.cinema == undefined || cineParams.cinema.length == 0) {
		res.jsonp([]);
		return;
	}
	request.get({
		uri: 'http://www.cineworld.com/api/quickbook/films',
		json: true,
		qs: cineParams
	}, function (err, response, body) {
		if(err) throw err;
		var params = {
			filmEDIs: _.pluck(body.films, 'edi')
		};
		if(params.filmEDIs.length == 0) {
			res.jsonp([]);
			return;
		}
		graph.query(graph.queries.getFilms, params, function (error, results) {
			if(error) throw error;
			var data = [];
			for(var i = 0, len = results.length; i < len; ++i) {
				data.push(results[i].film.data);
			}
			res.jsonp(data);
		});
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

app.get('/film', getFilms);
app.get('/film/:edi', getFilm);
app.get('/cinema/favs', ensureAuthenticated, getFavCinemas);
app.get('/cinema', getCinemas);
app.get('/cinema/:cinema/fav', ensureAuthenticated, favCinema);
app.get('/cinema/:cinema/unfav', ensureAuthenticated, unFavCinema);
app.post('/film/:edi/view', ensureAuthenticated, addView);

app.initialized = true;
listen();
function listen() {
	if(app.initialized && graph) {
		delete app.initialized;
		app.listen(process.env.PORT, function() {
			log.info("Express listening on %d", process.env.PORT);
		});
	}
}

function ensureAuthenticated(req, res, next) {
	if (req.isAuthenticated()) { return next(); }
	res.redirect('/login');
}