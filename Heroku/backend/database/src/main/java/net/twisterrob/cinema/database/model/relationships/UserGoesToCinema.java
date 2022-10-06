package net.twisterrob.cinema.database.model.relationships;

import org.neo4j.ogm.annotation.*;

import net.twisterrob.cinema.database.model.*;

//@RelationshipEntity(type = "GOESTO")
public class UserGoesToCinema {

	@Id
	@GeneratedValue
	public Long id;

	@StartNode
	public User user;

	@EndNode
	public Cinema cinema;
}
