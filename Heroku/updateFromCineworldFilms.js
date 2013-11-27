var request = require('request');     // https://github.com/mikeal/request
var log = require('./logs').task;
var neo4j = require('./neo4j');

neo4j.init(function(err, graph) {
	if(err) throw err;
	request.get({
		uri: 'http://www.cineworld.com/api/quickbook/films',
		json: true,
		qs: {
			key: "9qfgpF7B",
			full: true
		}
	}, function (err, response, body) {
		if(err) throw err;
		neo4j.createNodes(graph, 'Film', body.films, graph.queries.getAllFilms,
			function(filmNode) {
				return filmNode.film.data.edi;
			},
			function(film) {
				return film.edi;
			},
			function(filmToInsert) {
				filmToInsert.cineworldID = filmToInsert.id;
				delete filmToInsert.id;
			},
			function(error, filmNodes) {
				//console.log(filmNodes.length);
			}
		);
	});
});
