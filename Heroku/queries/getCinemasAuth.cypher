// {userID}: User.id
MATCH (c:Cinema)
WHERE not has (c._deleted)
OPTIONAL MATCH (c)<-[f:GOESTO]-(u:User { id:{userID} })
RETURN c as cinema, f IS NOT NULL as fav
