
plugins {
	id("org.jetbrains.kotlin.jvm") version "1.3.61"
}

repositories {
//	gradlePluginPortal()
	jcenter()
}

dependencies {
	implementation(kotlin("stdlib-jdk8", "1.3.61"))
//	implementation(kotlin("gradle-plugin", "1.3.61"))
//	implementation(kotlin("dsl", "0.17.5"))
}
