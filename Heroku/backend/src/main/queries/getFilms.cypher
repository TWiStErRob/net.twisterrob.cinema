// getFilms: find a list of films by edis
// {filmEDIs}: films to return
MATCH (f:Film)
WHERE not exists(f._deleted) and f.edi in {filmEDIs}
RETURN f as film
