MATCH (f:Film)
WHERE not has(f._deleted)
RETURN f as film
