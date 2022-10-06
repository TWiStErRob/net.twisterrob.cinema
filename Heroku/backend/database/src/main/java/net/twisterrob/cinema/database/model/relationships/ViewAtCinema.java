package net.twisterrob.cinema.database.model.relationships;

import org.neo4j.ogm.annotation.*;

import net.twisterrob.cinema.database.model.*;

//@RelationshipEntity(type = "AT")
public class ViewAtCinema {

	@Id
	@GeneratedValue
	public Long id;

	@StartNode
	public View view;

	@EndNode
	public Cinema cinema;
}
