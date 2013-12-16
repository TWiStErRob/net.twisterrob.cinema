// getUser: find the user by (Google)OpenID
// {userID}: OpenID
MATCH (u:User {id: {userID}})
RETURN u as user
