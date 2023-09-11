import net.twisterrob.cinema.build.publishing.publishSlimJar

plugins {
	id("org.gradle.application")
	id("net.twisterrob.cinema.build.dependencies")
	id("net.twisterrob.cinema.build.detekt")
	id("net.twisterrob.cinema.build.logging")
	id("net.twisterrob.cinema.build.compilation")
	id("net.twisterrob.cinema.build.testing")
	id("net.twisterrob.cinema.build.publishing")
	id("net.twisterrob.cinema.build.ide")
}

application {
	publishSlimJar()
}
