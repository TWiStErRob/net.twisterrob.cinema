// {id} OpenID
// {email}
// {name}: Display name
MERGE (u:User {id: {id}})
on create set u._created = timestamp()

on create set u.email = {email}
on match set u.email = {email}

on create set u.name = {name}
on match set u.name = {name}

RETURN u as user
