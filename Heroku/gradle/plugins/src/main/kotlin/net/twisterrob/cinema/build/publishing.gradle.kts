package net.twisterrob.cinema.build

import net.twisterrob.cinema.build.dsl.slug
import org.gradle.api.plugins.BasePluginExtension

configure<BasePluginExtension> {
	archivesName.set("twisterrob-cinema-${slug}")
}
