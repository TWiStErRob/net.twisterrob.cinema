//@file:RuntimeOptions("-J--illegal-access=deny")

// Based on:
// main.kts: https://kotlinlang.org/docs/custom-script-deps-tutorial.html
// HttpClient: https://ktor.io/docs/http-client-engines.html#java
// Jackson: https://ktor.io/docs/serialization-client.html > Jackson
// jackson {} config: https://www.baeldung.com/jackson-deserialize-json-unknown-properties
// Kml lib: https://web.archive.org/web/20170317215912/http://labs.micromata.de/projects/jak/quickstart.html
// Kml format: exported an example from [My Maps](https://www.google.com/maps/d).

// Implicit dependency: Java 11 (because ktor 2.x is compiled as Class 55)
// Note: normally these dependencies are listed without a -jvm suffix,
// but there's no Gradle resolution in play here, so we have to pick a platform manually.
@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-html-jvm:0.11.0")
@file:DependsOn("io.ktor:ktor-client-java-jvm:2.3.8")
@file:DependsOn("io.ktor:ktor-client-content-negotiation-jvm:2.3.8")
@file:DependsOn("io.ktor:ktor-serialization-jackson-jvm:2.3.8")
// Override transitively included jaxb-impl:2.2 to avoid warning when marshalling Kml.
// > Illegal reflective access by com.sun.xml.bind.v2.runtime.reflect.opt.Injector$1 (jaxb-impl-2.2.jar)
// > to method java.lang.ClassLoader.defineClass(java.lang.String,byte[],int,int)
@file:DependsOn("com.sun.xml.bind:jaxb-impl:4.0.4")
@file:DependsOn("de.micromata.jak:JavaAPIforKml:2.2.1")

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.micromata.opengis.kml.v_2_2_0.Document
import de.micromata.opengis.kml.v_2_2_0.Kml
import de.micromata.opengis.kml.v_2_2_0.Point
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.URLBuilder
import io.ktor.serialization.jackson.jackson
import kotlinx.coroutines.runBlocking
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.html
import kotlinx.html.p
import kotlinx.html.stream.createHTML
import java.io.File
import java.net.URI
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

data class CinemasResponse(
	val body: CinemasBody,
) {

	data class CinemasBody(
		val cinemas: List<Cinema>,
	) {

		data class Cinema(
			val id: String,
			val groupId: String,
			val displayName: String,
			val link: URI,
			val address: String,
			val latitude: Double,
			val longitude: Double,
		)
	}
}

fun ObjectMapper.cineworldConfig() {
	configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}

//<editor-fold desc="JAXB setup for modern Java" defaultstate="collapsed">
fun initJAXB() {
	// See https://stackoverflow.com/a/50251510/253468
	// In jaxb-impl 3.x and 4.x it is necessary to disable this optimization to allow running on Java 17.
	System.setProperty("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize", "true")

	// Disable logging about the optimization being disabled:
	// > Jul 11, 2023 5:06:10 PM com.sun.xml.bind.v2.runtime.reflect.opt.AccessorInjector <clinit>
	// > INFO: The optimized code generation is disabled

	// Need to keep a strong reference to the logger instance, otherwise the level customization gets garbage collected.
	val logger: java.util.logging.Logger = java.util.logging.Logger
		.getLogger("com.sun.xml.bind.v2.runtime.reflect.opt.AccessorInjector")

	logger.level = java.util.logging.Level.OFF
	// Load the class to trigger the static initializer that logs the above message.
	Class.forName("com.sun.xml.bind.v2.runtime.reflect.opt.AccessorInjector")
	logger.level = null
}
//</editor-fold>

fun loadFile(): List<CinemasResponse.CinemasBody.Cinema> {
	val client = jacksonObjectMapper().apply { cineworldConfig() }.reader()
	return client.readValue(File("cinemas.json"), CinemasResponse::class.java).body.cinemas
}

fun loadNetwork(now: LocalDate): List<CinemasResponse.CinemasBody.Cinema> {
	val client = HttpClient {
		install(ContentNegotiation) {
			jackson {
				cineworldConfig()
			}
		}
		expectSuccess = true
	}

	fun cinemas(url: String): List<CinemasResponse.CinemasBody.Cinema> {
		println("Loading... ${url}")
		return runBlocking { client.get(url).body<CinemasResponse>().body.cinemas }
	}

	val nextYear = now.plusYears(1).toString()

	@Suppress("MaxLineLength")
	val uk =
		cinemas("https://www.cineworld.co.uk/uk/data-api-service/v1/quickbook/10108/cinemas/with-event/until/${nextYear}?attr=&lang=en_GB")

	@Suppress("MaxLineLength")
	val ie =
		cinemas("https://www.cineworld.ie/ie/data-api-service/v1/quickbook/10109/cinemas/with-event/until/${nextYear}?attr=&lang=en_IE")
	return uk + ie
}

val now = LocalDate.now()
val cinemas = loadNetwork(now)
//println(cinemas.map { it.displayName })

val kml = Kml().apply {
	feature = Document().apply {
		name = "Cineworld Cinemas"
		val niceNow = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(now)
		description = "List of all the cineworld cinemas in the UK on ${niceNow}."
		createAndAddStyle().apply {
			id = "cineworld-placemark"
			createAndSetIconStyle().apply {
				scale = 1.0
				createAndSetIcon().apply {
					href = "https://www.google.com/s2/favicons?domain=www.cineworld.co.uk"
				}
			}
		}
		createAndAddFolder().apply {
			name = "Cineworld Cinemas"
			cinemas.forEach { cinema ->
				createAndAddPlacemark().apply {
					name = cinema.displayName
					description = createHTML(prettyPrint = false).html {
						body {
							p {
								+"Website: "
								a(href = cinema.link.toString(), target = "_blank") { +cinema.link.toString() }
							}
							p {
								+"Address: "
								val mapSearch = URLBuilder("https://maps.google.com/maps").apply {
									parameters.append("q", cinema.address)
								}.buildString()
								a(href = mapSearch, target = "_blank") { +cinema.address }
							}
						}
					}.toString()
					address = cinema.address
					geometry = Point().addToCoordinates(cinema.longitude, cinema.latitude)
					styleUrl = "#cineworld-placemark"
				}
			}
		}
	}
}

val kmlFile = File("cinemas.kml").apply {
	delete()
	initJAXB()
	kml.marshal(this)
	writeText(readText().replace("ns2:", "").replace("xmlns:ns2", "xmlns"))
}
//print(kmlFile.readText())
println(kmlFile.absolutePath)
