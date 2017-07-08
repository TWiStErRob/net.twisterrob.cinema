// addUser: Create a user if doesn't exists with the specified data
// {id} OpenID
// {email}
// {name}: Display name
// {created}: moment of creation
MERGE (u:User {id: {id}})
on create set u._created = {created}

on create set u.email = {email}
on create set u.name = {name}
on create set u.realm = {realm}

on match set u.email = {email}
on match set u.name = {name}
on match set u.realm = {realm}

RETURN u as user
