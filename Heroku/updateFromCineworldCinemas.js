var request = require('request');     // https://github.com/mikeal/request
var log = require('./logs').task;
var neo4j = require('./neo4j');

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
		neo4j.createNodes(graph, 'Cinema', body.cinemas, graph.queries.getAllCinemas,
			"cinema",
			function(cinemaNode) {
				return cinemaNode.data.id;
			},
			"cineworldID",
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
