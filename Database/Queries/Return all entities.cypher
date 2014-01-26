// Return all entities
match (u:User) return u as node
union
match (v:View) return v as node
union
match (c:Cinema) return c as node
union
match (f:Film) return f as node
