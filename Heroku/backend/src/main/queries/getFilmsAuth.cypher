// getFilms: find a list of films by edis
// {filmEDIs}: films to return
// {userID}: User.id
MATCH (f:Film)
WHERE //not exists(f._deleted) and
f.edi in {filmEDIs}
OPTIONAL MATCH (f)<-[w:WATCHED]-(v:View)
OPTIONAL MATCH (v)<-[a:ATTENDED]-(u:User { id:{userID} })
OPTIONAL MATCH (v)-[at:AT]->(c:Cinema)
RETURN
	f as film,
	coalesce(v, f) as view,
	coalesce(u, f) as user,
	coalesce(c, f) as cinema
