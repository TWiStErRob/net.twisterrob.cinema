var request = require('request');     // https://github.com/mikeal/request
var log = require('./logs').task;
var neo4j = require('./neo4j');
var _ = require('underscore');

neo4j.init(function(err, graph) {
	if(err) throw err;
	request.get({
		uri: 'https://www.cineworld.co.uk/api/quickbook/cinemas',
		json: true,
		qs: {
			key: "9qfgpF7B",
			full: true
		}
		/*
		address: "Queens Links Leisure Park, Links Road, Aberdeen"
		cinema_url: "http://www.cineworld.co.uk/cinemas/1/information"
		id: 1
		name: "Aberdeen - Queens Links"
		postcode: "AB24 5EN"
		telephone: "0871 200 2000"
		*/
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
