MATCH (f:Film)
WHERE not has(f._deleted) and f.edi in {filmEDIs}
RETURN f as film
