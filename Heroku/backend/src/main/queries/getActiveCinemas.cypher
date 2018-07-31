// getActiveCinemas: return all Cinemas which are active
MATCH (c:Cinema)
WHERE not exists (c._deleted)
RETURN c as cinema
