package net.twisterrob.cinema.database.model.relationships;

import org.neo4j.ogm.annotation.*;

import net.twisterrob.cinema.database.model.*;

//@RelationshipEntity(type = "ATTENDED")
public class UserAttendedView {

	@Id
	@GeneratedValue
	public Long id;

	@StartNode
	public User user;

	@EndNode
	public View view;
}
