// {userID}: OpenID
MATCH (u:User {id: {userID}})
RETURN u as user
