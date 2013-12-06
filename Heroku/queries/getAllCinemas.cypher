MATCH (c:Cinema)
WHERE not has (c._deleted)
RETURN c as cinema
