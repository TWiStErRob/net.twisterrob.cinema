var fs = require('fs');               // http://nodejs.org/api/fs.html
var http = require('http');           // http://nodejs.org/api/http.html
var url = require('url');             // http://nodejs.org/api/url.html
var express = require('express');     // http://expressjs.com/api.html
var extend = require('node.extend');  // https://github.com/dreamerslab/node.extend
var _ = require('underscore');        // http://underscorejs.org/
process.env.NODE_DEBUG = ''; // request neo4j express
var request = require('request');     // https://github.com/mikeal/request
var qs = require('querystring');      // http://nodejs.org/api/querystring.html
var async = require('async');         // https://github.com/caolan/async
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
		dateEpochUTC: parseInt(req.param('date'), 10),
		userID: req.user.id
	};
	graph.query(graph.queries.addView, params, function (error, results) {
		if(error) throw error;
		if(results.length !== 1) {
			res.send(404, 'No or more results found: ' + results.length);
		} else {
			var result = results[0];
			res.jsonp(_.extend({}, result.view.data, {
				film: result.film.data,
				cinema: result.cinema.data,
				user: result.user.data,
			}));
		}
	});
}

function removeView(req, res) {
	var params = {
		filmEDI: parseInt(req.param('edi'), 10),
		userID: req.user.id
	};
	graph.query(graph.queries.removeView, params, function (error, results) {
		if(error) throw error;
		res.send(200);
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
		cinema: req.paramArr('cinemaIDs')
	};
	if(cineParams.date === undefined
	|| cineParams.cinema === undefined || cineParams.cinema.length === 0) {
		res.jsonp([]);
		return;
	}
	request.get({
		// need to build url manually because visionmedia's qs (used by request) adds indices to arrays (cinema)
		uri: 'http://www.cineworld.com/api/quickbook/films?' + qs.stringify(cineParams),
		json: true
	}, function (err, response, body) {
		if(err) throw err;
		var params = {
			filmEDIs: _.pluck(body.films, 'edi')
		};
		if(params.filmEDIs.length == 0) {
			res.jsonp([]);
			return;
		}
		if (req.isAuthenticated()) {
			params = _.extend(params, {
				userID: req.user.id
			});
			graph.query(graph.queries.getFilmsAuth, params, function (error, results) {
				if(error) throw error;
				var data = [];
				for(var i = 0, len = results.length; i < len; ++i) {
					var result = results[i];
					var f = _.clone(result.film.data);
					if(!_.isEmpty(result.view) && !_.isEmpty(result.view.data)
							&& result.view.data.class != 'Film') { // workaround for neo4j wrapper not liking nulls
						f.view = _.extend({}, result.view.data, {
							film: result.film.data, // using f would be circular
							cinema: result.cinema.data,
							user: result.user.data,
						});
					} else {
						f.view = null;
					}
					data.push(f);
				}
				res.jsonp(data);
			});
		} else {
			graph.query(graph.queries.getFilms, params, function (error, results) {
				if(error) throw error;
				var data = [];
				for(var i = 0, len = results.length; i < len; ++i) {
					var result = results[i];
					var f = result.film.data;
					data.push(f);
				}
				res.jsonp(data);
			});
		}
	});
}

function getPerformances(req, res) {
	var perfParams = {
		key: "9qfgpF7B",
		date: req.param('date'),
		cinema: req.paramArr('cinemaIDs'),
		film: req.paramArr('filmEDIs'),
	};
	if(perfParams.date   === undefined
	|| perfParams.cinema === undefined || perfParams.cinema.length === 0
	|| perfParams.film   === undefined || perfParams.film.length   === 0) {
		res.jsonp([]);
		return;
	}
	perfParams.cinema = _.map(perfParams.cinema, function(x) { return parseInt(x, 10); });
	perfParams.film = _.map(perfParams.film, function(x) { return parseInt(x, 10); });
	
	var combinations = [];
	for(var c = 0, cLen = perfParams.cinema.length; c < cLen; c++) {
		var cinema = perfParams.cinema[c];
		for(var f = 0, fLen = perfParams.film.length; f < fLen; f++) {
			var film = perfParams.film[f];
			combinations.push({
				date: perfParams.date,
				cinema: cinema,
				film: film
			});
		}
	}
	var reqs = _.map(combinations, function(combo) {
		return {
			uri: 'http://www.cineworld.com/api/quickbook/performances',
			json: true,
			qs: {
				key: perfParams.key,
				date: combo.date,
				cinema: combo.cinema,
				film: combo.film
			}
		};
	});
	var tasks = _.map(reqs, function(req) {
		return function(callback) {
			return request.get(req, callback);
		};
	});
	async.parallelLimit(tasks, 3, function (err, results) {
		if(err) throw err;
		// [ [req, body], [req, body], [req, body], ... ]
		results = _.pluck(results, 1);
		// [ /* body */{ errors: [], performances: [] }, /* body */{ errors: [], performances: [] }, ... ]
		var errors = _.flatten(_.pluck(results, 'errors'));
		if(_.compact(errors).length > 0) { // ignore empty arrays and undefineds
			res.send(500, errors.join("\n"));
		}
		var performances = _.pluck(results, 'performances');
		var responseArr = _.map(_.zip(combinations, performances), function(pair) {
			return {
				date: pair[0].date,
				cinema: pair[0].cinema,
				film: pair[0].film,
				performances: pair[1]
			};
		});
		res.send(responseArr);
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
	app.use(function extendReq(req, res, next) {
		req.paramArr = function(name) {
			var param = this.param(name);
			return param === undefined? [] : (_.isArray(param)? param : [param]);
		};
		return next();
	});
	app.use(express.compress());
	app.use(express.static(__dirname + '/static/'));
	if(process.env.NODE_DEBUG && /\bexpress\b/g.test(process.env.NODE_DEBUG)) {
		app.use(express.logger());
	}
	app.use(express.cookieParser());
	app.use(express.bodyParser());
	app.use(express.cookieSession({
		key: 'session',
		secret: 'twister'
	}));
});

auth.init(app);

var cacheLength = 0 * 60 * 60;
app.get('/film', cacher(cacheLength), getFilms);
app.get('/film/:edi', getFilm);
app.get('/cinema/favs', ensureAuthenticated, getFavCinemas);
app.get('/cinema', cacher(cacheLength), getCinemas);
app.get('/cinema/:cinema/fav', ensureAuthenticated, favCinema);
app.get('/cinema/:cinema/unfav', ensureAuthenticated, unFavCinema);
app.get('/performance', cacher(cacheLength), getPerformances);
app.get('/film/:edi/view', ensureAuthenticated, addView);
app.get('/film/:edi/unview', ensureAuthenticated, removeView);

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
	res.send(401, 'Please log in first to access this feature.');
}

function cacher(length) {
	if(length) {
		return function addCacheHeaders(req, res, next) {
			res.setHeader("Cache-Control", "public, max-age=" + length);
			res.setHeader("Expires", new Date(Date.now() + (length * 1000)).toUTCString());
			return next();
		};
	} else {
		return function(req, res, next) { return next(); };
	}
}