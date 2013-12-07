// {cinemaID}: Cinema.cineworldID
// {userID}: User.id
MATCH
	(c:Cinema { cineworldID:{cinemaID} }),
	(u:User { id:{userID} })
CREATE UNIQUE
	(u)-[:GOESTO]->(c)
RETURN u as user, c as cinema
