package net.twisterrob.cinema.build

import net.twisterrob.cinema.build.dsl.slug

plugins {
	id("org.gradle.base")
}

base {
	archivesName = "twisterrob-cinema-${project.slug}"
}
