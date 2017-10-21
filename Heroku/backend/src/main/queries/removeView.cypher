// removeView: remove the Views for a given film for a user
// {cinemaID}: Cinema.cineworldID
// {filmEDI}: Film.edi
// {userID}: User.id
// {dateEpochUTC}: View.date
MATCH (v:View {date: {dateEpochUTC} })
MATCH (v)<-[a:ATTENDED]-(u:User { id: {userID} })
MATCH (v)-[w:WATCHED]->(f:Film { edi: {filmEDI} })
MATCH (v)-[at:AT]->(c:Cinema { cineworldID: {cinemaID} })
MATCH (v)-[r]-()
DELETE v, r
