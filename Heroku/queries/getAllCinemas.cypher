START c=node(*)
WHERE c.class! = 'Cinema'
  and not has (c._deleted)
RETURN c as cinema
