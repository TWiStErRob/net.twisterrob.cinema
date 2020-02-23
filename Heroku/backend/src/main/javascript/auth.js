var neo4j = require('neo4j-js');      // https://github.com/bretcope/neo4j-js/blob/master/docs/Documentation.md
                                      // https://github.com/bretcope/neo4j-js/blob/master/docs/REST.md
var GoogleStrategy = require('passport-google-oauth20').Strategy; // http://passportjs.org/docs/google
var config = require('./config');
var moment = require('moment');
var log = require('./logs').auth;

// TODO add returnUrl handling
function ensureAuthenticated(req, res, next) {
	if (req.isAuthenticated()) {
		return next();
	}
	res.redirect('/login');
}

function setupPassport(passport, app, graph) {
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
				done("User " + id + " not found", null);
			} else {
				done(error, results[0].user.data);
			}
		});
	});

	passport.use(new GoogleStrategy({
			clientID: config.GOOGLE_CLIENT_ID,
			clientSecret: config.GOOGLE_CLIENT_SECRET,
			callbackURL: app.get('app urlRoot') + 'auth/google/return'
		},
		function(accessToken, refreshToken, profile, done) {
			graph.query(graph.queries.addUser, {
				id: profile.id,
				email: profile.emails[0].value,
				name: profile.displayName,
				realm: app.get('app urlRoot'),
				created: moment().format()
			}, function (error, results) {
				if(error) {
					return done(error, undefined);
				} else if(!results.length) {
					return done(undefined, false);
				} else {
					return done(undefined, results[0].user.data);
				}
			});
		}
	));
}

function setupRoutes(passport, app) {
	app.get('/account', ensureAuthenticated, function getUser(req, res){
		res.jsonp({ user: req.user });
	});
	app.get('/login', function login(req, res){
		res.redirect('/auth/google');
	});

	const googleAuthSettings = { scope: ['openid', 'email', 'profile'], failureRedirect: '/login' };
	app.get('/auth/google',
		function skipLogin(req, res, next) {
			if (req.isAuthenticated()) {
				log.trace({user: req.user}, 'Already logged in as %s.', req.user.id);
				res.redirect('/');
			} else {
				next();
			}
		},
		passport.authenticate('google', googleAuthSettings)
	);
	app.get('/auth/google/return',
		passport.authenticate('google', googleAuthSettings),
		function successfulLogin(req, res) {
			log.trace({user: req.user}, 'Successful login: %s.', req.user.id);
			res.redirect('/');
		}
	);

	app.get('/logout', function logout(req, res) {
		if(req.isAuthenticated()) {
			log.trace({user: req.user}, 'Logging out %s.', req.user.id);
			req.logout();
			res.redirect('/');
		} else {
			log.warn('Already logged out.');
			res.redirect('/');
		}
	});
}

module.exports.init = function(passport, app, graph) {
	setupPassport(passport, app, graph);
	setupRoutes(passport, app);
};
