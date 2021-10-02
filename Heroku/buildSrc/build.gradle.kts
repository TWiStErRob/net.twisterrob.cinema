plugins {
	`kotlin-dsl`
}

repositories {
	jcenter()
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
	implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.18.1")
}

kotlinDslPluginOptions {
	experimentalWarning.set(false)
}
