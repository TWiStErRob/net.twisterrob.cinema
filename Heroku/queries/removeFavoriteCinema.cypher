// removeFavoriteCinema: Find the User and remove the relation GOESTO to the Cinema
// {cinemaID}: Cinema.cineworldID
// {userID}: User.id
MATCH
	(u:User { id:{userID} })-[g:GOESTO]->(c:Cinema { cineworldID:{cinemaID} })
DELETE g
RETURN u as user, c as cinema
