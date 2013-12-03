START f=node(*)
WHERE f.class! = 'Film'
  and not has(f._deleted)
RETURN f as film
