START c=node(*)
WHERE c.class! = 'Cinema'
RETURN c as cinema
