// {filmEDI}: Film.edi
MATCH (f:Film {edi: {filmEDI}})
RETURN f as film
