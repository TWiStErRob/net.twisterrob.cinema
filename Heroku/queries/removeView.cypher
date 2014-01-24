// removeView: remove the Views for a given film for a user
// {filmEDI}: Film.edi
// {userID}: User.id
MATCH (v:View)
MATCH (v)<-[a:ATTENDED]-(u:User { id:{userID} })
MATCH (v)-[w:WATCHED]->(f:Film { edi:{filmEDI} })
MATCH (v)-[r]-()
DELETE v, r
