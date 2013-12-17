var request = require('request');     // https://github.com/mikeal/request
var log = require('./logs').task;
var neo4j = require('./neo4j');
var _ = require('underscore');

neo4j.init(function(err, graph) {
	if(err) throw err;
	request.get({
		uri: 'http://www.cineworld.com/api/quickbook/cinemas',
		json: true,
		qs: {
			key: "9qfgpF7B",
			full: true
		}
	}, function (err, response, body) {
		if(err) throw err;
		_.each(body.cinemas, function(cinema) {
			cinema.cineworldID = cinema.id; delete cinema.id;
		});
		neo4j.createNodes(graph, 'Cinema', body.cinemas, graph.queries.getAllCinemas,
			"cinema",
			function(cinemaNode) {
				return cinemaNode.data.cineworldID;
			},
			"cineworldID",
			function(error, createdNodes, updatedNodes, deletedNodes) {
				if(error) log.error(error);
			}
		);
	});
});
