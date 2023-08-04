plugins {
	id("net.twisterrob.cinema.container")
	id("org.gradle.idea")
}

idea {
	module {
		excludeDirs.add(layout.projectDirectory.dir("frontend/static").asFile)
	}
}
