// Create a view
MATCH (c:Cinema { cineworldID:58 }),(f:Film { edi:43483 }),(u:User { name:"twister" }) 
CREATE UNIQUE (u)-[:ATTENDED]->(v),(c)<-[:AT]-(v),(v)-[:WATCHED]->(f) 
SET v:View 
RETURN v, u, c, f