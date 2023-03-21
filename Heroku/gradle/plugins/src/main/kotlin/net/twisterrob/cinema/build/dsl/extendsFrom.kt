package net.twisterrob.cinema.build.dsl

import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.artifacts.Configuration

fun NamedDomainObjectProvider<Configuration>.extendsFrom(other: NamedDomainObjectProvider<Configuration>) {
	this.configure { extendsFrom(other.get()) }
}
