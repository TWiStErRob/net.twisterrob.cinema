package net.twisterrob.cinema.cineworld.sync.syndication

import org.junit.jupiter.api.Assertions.assertNotNull

fun Feed.sanityCheck() {
	verifyAllAttributesAreValid()
	verifyCalculatedProperties()
}

fun Feed.verifyAllAttributesAreValid() {
	this.attributes.forEach { attribute ->
		assertNotNull(attribute.code)
		assertNotNull(attribute.title)
	}
}

fun Feed.verifyCalculatedProperties() {
	this.films.forEach { it.attributeList }
	this.cinemas.forEach { it.serviceList }
	this.performances.forEach { it.attributeList }
}
