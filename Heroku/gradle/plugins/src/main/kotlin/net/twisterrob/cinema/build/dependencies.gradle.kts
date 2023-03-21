package net.twisterrob.cinema.build

import configureDependencyLocking
import forceKotlinVersion
import net.twisterrob.cinema.build.dsl.slug

project.configureDependencyLocking()
project.forceKotlinVersion()

plugins.withId("java") {
	configure<BasePluginExtension> {
		archivesName.set("twisterrob-cinema-${slug}")
	}
}
