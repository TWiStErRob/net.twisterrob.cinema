// getActiveFilms: return all Films which are active
MATCH (f:Film)
WHERE not exists(f._deleted)
RETURN f as film
