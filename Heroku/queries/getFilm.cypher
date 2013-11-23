// {node}: node id
START root = node(*)
MATCH path = (root-[SAW]->film)
WHERE root.type! = 'user'
RETURN film.edi
