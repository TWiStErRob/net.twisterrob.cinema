var fs = require('fs');               // http://nodejs.org/api/fs.html
var http = require('http');           // http://nodejs.org/api/http.html
var url = require('url');             // http://nodejs.org/api/url.html
var express = require('express');     // http://expressjs.com/api.html
var extend = require('node.extend');  // https://github.com/dreamerslab/node.extend
var bunyan = require('bunyan');
var neo4j = require('neo4j-js');      // https://github.com/bretcope/neo4j-js/blob/master/docs/Documentation.md
                                      // https://github.com/bretcope/neo4j-js/blob/master/docs/REST.md
var passport = require('passport');   // http://passportjs.org/guide/
var GoogleStrategy = require('passport-google').Strategy; // http://passportjs.org/guide/google/
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

passport.serializeUser(function(user, done) {
	done(null, user.id);
});

passport.deserializeUser(function(id, done) {
	graph.query(graph.queries.getUser, {
		userID: id
	}, function (error, results) {
		if(error) {
			done(error, undefined);
		} else if(!results.length) {
			done("User not found", null);
		} else {
			done(error, results[0].user.data);
		}
	});
});

passport.use(new GoogleStrategy({
		returnURL: app.get('app urlRoot') + 'auth/google/return',
		realm: app.get('app urlRoot')
	},
	function(identifier, profile, done) {
		// asynchronous verification, for effect...
		process.nextTick(function validate() {
			graph.query(graph.queries.addUser, {
				id: identifier,
				email: profile.emails[0].value,
				name: profile.displayName
			}, function (error, results) {
				console.log(results);
				if(error) {
					return done(error, undefined);
				} else {
					return done(undefined, results[0].user.data);
				}
			});
		});
	}
));

app.configure(function configure_use() {
	app.use(express.compress());
	app.use(express.static(__dirname + '/static/'));
	app.use(express.logger());
	app.use(express.cookieParser());
	app.use(express.bodyParser());
	app.use(express.cookieSession({
		key: 'session',
		secret: 'twister'
	}));
	app.use(passport.initialize());
	app.use(passport.session());
});

app.get('/account', ensureAuthenticated, function(req, res){
	res.jsonp({ user: req.user });
});
app.get('/login', function(req, res){
	res.send('<a href="/auth/google">Google OpenID</a>');
});

app.get('/auth/google',
	passport.authenticate('google', { failureRedirect: '/login' }),
	function(req, res) {
		res.redirect('/');
	}
);
app.get('/auth/google/return',
	passport.authenticate('google', { failureRedirect: '/login' }),
	function(req, res) {
		res.redirect('/');
	}
);

app.get('/logout', function(req, res) {
	req.logout();
	res.redirect('/');
});

app.get('/film/:edi', getFilm);
app.post('/film/:edi/view', addView);
app.listen(process.env.PORT, function() {
	log.info("Express listening on %d", process.env.PORT);
});

function ensureAuthenticated(req, res, next) {
	if (req.isAuthenticated()) { return next(); }
	res.redirect('/login');
}