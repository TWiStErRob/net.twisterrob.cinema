// addView: create a relationship node for  "User watched Film in Cinema"
// {cinemaID}: Cinema.cineworldID
// {filmEDI}: Film.edi
// {userID}: User.id
// {dateEpochUTC}: View.date
MATCH
	(c:Cinema { cineworldID:{cinemaID} }),
	(f:Film { edi:{filmEDI} }),
	(u:User { id:{userID} })
CREATE UNIQUE
	(u)-[:ATTENDED]->(v:View {
		film: {filmEDI},
		cinema: {cinemaID},
		user: {userID},
		date: {dateEpochUTC}
	}),
	(v:View)-[:AT]->(c),
	(v:View)-[:WATCHED]->(f)
RETURN v as view, u as user, c as cinema, f as film
