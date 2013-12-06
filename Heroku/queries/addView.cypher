// {cinemaID}: Cinema.cineworldID
// {filmEDI}: Film.edi
// {userID}: User.id
MATCH
	(c:Cinema { cineworldID:{cinemaID} }),
	(f:Film { edi:{filmEDI} }),
	(u:User { name:{userID} })
CREATE UNIQUE
	(u)-[:ATTENDED]->(v:View {
		film: {filmEDI},
		cinema: {cinemaID},
		user: {userID}
	}),
	(v:View)-[:AT]->(c),
	(v:View)-[:WATCHED]->(f)
RETURN v as view, u as user, c as cinema, f as film
