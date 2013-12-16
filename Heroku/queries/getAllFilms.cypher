// getAllFilms: return all Films which are active
MATCH (f:Film)
WHERE not has(f._deleted)
RETURN f as film
