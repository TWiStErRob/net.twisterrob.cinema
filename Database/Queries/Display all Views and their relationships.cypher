// Display ALL views and their rels
MATCH (v:View)-[r]-()
RETURN v, r