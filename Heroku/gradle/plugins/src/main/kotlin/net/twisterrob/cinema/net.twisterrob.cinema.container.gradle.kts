plugins {
	id("net.twisterrob.cinema.build.ide")
}

// Make sure this project plays in lockfile writing to prevent missing lockfiles for diff.
configurations.maybeCreate("empty")
