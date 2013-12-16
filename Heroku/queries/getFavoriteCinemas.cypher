// getFavoriteCinemas: get all the Cinemas associated with a User
// {userID}: User.id
MATCH
	(u:User { id:{userID} })-[:GOESTO]->(c:Cinema)
RETURN c as cinema
