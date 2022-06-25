/**
 * Rules:
 *  * `val Library = deps.Library` to have a central place (and also to prevent "UnnecessaryQualifiedReference"
 *  * `object Library` on top level in deps package, so Deps.* auto-complete is clean
 *  * `object Library` contains a `version` constant
 *  * `object Library` contains a `core` constant
 */
@Suppress("unused")
object Deps {

	val Kotlin = deps.Kotlin
	val Ktor = deps.Ktor
	val Dagger2 = deps.Dagger2
	val Neo4JOGM = deps.Neo4JOGM
	val SLF4J = deps.SLF4J
	val Log4J2 = deps.Log4J2
	val Jackson = deps.Jackson
	val JUnit = deps.JUnit
	val Hamcrest = deps.Hamcrest
	val JFixture = deps.JFixture
	val Mockito = deps.Mockito
}
