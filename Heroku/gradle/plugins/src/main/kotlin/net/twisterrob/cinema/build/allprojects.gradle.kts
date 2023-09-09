package net.twisterrob.cinema.build

import net.twisterrob.cinema.build.dsl.isCI

plugins {
	id("org.gradle.idea")
}

idea {
	module {
		isDownloadSources = !isCI
		isDownloadJavadoc = !isCI
	}
}
