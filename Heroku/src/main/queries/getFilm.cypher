// getFilm: find a film by edi
// {filmEDI}: Film.edi
MATCH (f:Film {edi: {filmEDI}})
RETURN f as film
