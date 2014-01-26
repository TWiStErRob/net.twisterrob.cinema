// Delete ALL views and their rels
MATCH (v:View)-[r]-()
DELETE v, r