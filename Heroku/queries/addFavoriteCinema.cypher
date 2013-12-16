// addFavoriteCinema: create a GOESTO relation between User and Cinema if it doesn't exist
// {cinemaID}: Cinema.cineworldID
// {userID}: User.id
MATCH
	(c:Cinema { cineworldID:{cinemaID} }),
	(u:User { id:{userID} })
CREATE UNIQUE
	(u)-[:GOESTO]->(c)
RETURN u as user, c as cinema
