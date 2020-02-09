package net.twisterrob.cinema.database.model

import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Property
import org.neo4j.ogm.annotation.Relationship
import org.neo4j.ogm.annotation.typeconversion.DateLong
import java.time.Instant

@NodeEntity(label = "View")
class View(
	@Property(name = "film")
	var film: Long,

	@Property(name = "cinema")
	var cinema: Long,

	@Property(name = "user")
	var user: String
) : BaseNode() {

	constructor(
		film: Film,
		cinema: Cinema,
		user: User,
		time: Instant
	) : this(film = film.edi, cinema = cinema.cineworldID, user = user.id) {
		this.watchedFilm = film
		this.atCinema = cinema
		this.userRef = user
		this.date = time
	}

	@Property(name = "class")
	val className: String = "View"

	@Property(name = "date")
	@DateLong
	lateinit var date: Instant

	@Relationship(type = "AT")
	lateinit var atCinema: Cinema

	@Relationship(type = "WATCHED")
	lateinit var watchedFilm: Film

	@Relationship(type = "ATTENDED", direction = Relationship.INCOMING)
	lateinit var userRef: User

	//	@Override public String toString() {
	//		return String.format("View(%d, %s watched %s at %s)", id, user.name, film.title, cinema.name);
	//	}

	//	@Convert(InstantConverter.class)
	//	@Property(name = "created")
	//	public Instant created;
	//
	//	@Relationship(type = "PORTRAYS", direction = Relationship.INCOMING)
	//	public Set<Image> images = new HashSet<>();
	//
	//	@Relationship(type = "SHOWS", direction = Relationship.INCOMING)
	//	public Set<Video> videos = new HashSet<>();
	//
	//	@Convert(value = ExtrasConverter.class)
	//	public final Map<String, ?> extras = new HashMap<>();
}
