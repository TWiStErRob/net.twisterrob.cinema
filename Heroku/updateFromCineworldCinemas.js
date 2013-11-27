var request = require('request');     // https://github.com/mikeal/request
var log = require('./logs').task;
var neo4j = require('./neo4j');

neo4j.init(function(graph) {
	request.get({
		uri: 'http://www.cineworld.com/api/quickbook/cinemas',
		json: true,
		qs: {
			key: "9qfgpF7B",
			full: true
		}
	}, function (error, response, body) {
		if(error) {
			throw error;
		}
		neo4j.createNodes(graph, 'Cinema', body.cinemas, graph.queries.getAllCinemas,
			function(cinemaNode) {
				return cinemaNode.id;
			},
			function(cinema) {
				return cinema.cineworldID;
			},
			function(cinemaToInsert) {
				cinemaToInsert.cineworldID = cinemaToInsert.id;
				delete cinemaToInsert.id;
			},
			function(error, cinemaNodes) {
				//console.log(cinemaNodes.length);
			}
		);
	});
});
