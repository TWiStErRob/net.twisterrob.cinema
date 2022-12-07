package net.twisterrob.cinema.cineworld.sync.syndication

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.util.InternalAPI
import io.ktor.util.rootCause
import net.twisterrob.test.assertAll
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.net.URI
import java.time.LocalDate
import java.time.OffsetDateTime

class JacksonSerializationTest {

	data class SubelementsAndAttributes(
		@JacksonXmlProperty(isAttribute = true)
		val attr1: String,

		@JsonProperty(index = 1)
		val sub1: Int,

		@JacksonXmlProperty(isAttribute = true)
		val attr2: Int,

		@JsonProperty(index = 2)
		val sub2: String
	)

	@Test fun `SubelementsAndAttributes serialization is reversible`() {
		val sut = jackson()

		val data = SubelementsAndAttributes(
			attr1 = "Attribute 1",
			sub1 = 2,
			attr2 = 3,
			sub2 = "Element 4"
		)
		@Language("xml")
		val xml = """
			<SubelementsAndAttributes attr1="Attribute 1" attr2="3">
			  <sub1>2</sub1>
			  <sub2>Element 4</sub2>
			</SubelementsAndAttributes>
		""".trimIndent()

		testSerialization(sut, data, xml)
	}

	@JsonIdentityInfo(
		scope = IdAsAttribute::class,
		generator = ObjectIdGenerators.PropertyGenerator::class,
		property = "id"
	)
	data class IdAsAttribute(
		@JacksonXmlProperty(isAttribute = true)
		val id: Long
	)

	@Test fun `IdAsAttribute serialization is reversible`() {
		val sut = jackson()

		val data = IdAsAttribute(
			id = 42
		)
		@Language("xml")
		val xml = """
			<IdAsAttribute id="42"/>
		""".trimIndent()

		testSerialization(sut, data, xml)
	}

	data class SplitStringValues(
		/**
		 * @sample `"a,bb,ccc"`
		 */
		val vals: String
	) {

		@JsonIgnore
		val valList = vals.split(",")

		constructor(vals: List<String>) : this(vals.joinToString(","))
	}

	@Test fun `SplitStringValues serialization is reversible`() {
		val sut = jackson()

		val data = SplitStringValues(
			vals = listOf("a", "bb", "ccc")
		)
		@Language("xml")
		val xml = """
			<SplitStringValues>
			  <vals>a,bb,ccc</vals>
			</SplitStringValues>
		""".trimIndent()

		val actual = testSerialization(sut, data, xml)

		assertEquals(listOf("a", "bb", "ccc"), actual.valList)
	}

	data class References(
		val referenced: Referenced,
		val referencing: Referencing,
	) {

		data class Referencing(
			@JacksonXmlProperty(isAttribute = true)
			val id: Long
		) {

			// missing from serialized
			@Suppress("DataClassShouldBeImmutable")
			@JsonManagedReference("refName")
			@JsonIdentityReference(alwaysAsId = true)
			@JacksonXmlProperty(isAttribute = true)
			private lateinit var reference: Referenced

			constructor(id: Long, reference: Referenced) : this(id) {
				this.reference = reference
			}
		}

		@JsonIdentityInfo(
			scope = Referenced::class,
			generator = ObjectIdGenerators.PropertyGenerator::class,
			property = "id"
		)
		data class Referenced(
			@JacksonXmlProperty(isAttribute = true)
			val id: Long
		) {

			@Suppress("DataClassShouldBeImmutable")
			@JsonBackReference("refName")
			lateinit var referencing: Referencing
		}
	}

	@Test fun `References serialization is reversible`() {
		val sut = jackson()
		val referenced = References.Referenced(42)
		val referencing = References.Referencing(43, referenced)
		referenced.referencing = referencing

		val data = References(
			referenced = referenced,
			referencing = referencing
		)
		@Language("xml")
		val xml = """
			<References>
			  <referenced id="42"/>
			  <referencing id="43" reference="42"/>
			</References>
		""".trimIndent()

		testSerialization(sut, data, xml)
	}

	@JacksonXmlRootElement(localName = "parent")
	data class RootElementWithChildren(
		@JacksonXmlElementWrapper(localName = "element1s")
		@JacksonXmlProperty(localName = "element1")
		@JsonProperty(index = 1)
		val element1s: List<Element1>,

		@JacksonXmlElementWrapper(localName = "element2s")
		@JacksonXmlProperty(localName = "element2")
		@JsonProperty(index = 2)
		val element2s: List<Element2>,
	) {

		data class Element1(
			val content: String
		)

		data class Element2(
			val content: String
		)
	}

	// TODEL https://github.com/FasterXML/jackson-module-kotlin/issues/153
	@Disabled("JacksonXmlProperty localName is used for serialization correctly, but breaks deserialization")
	@Test fun `RootElementWithChildren serialization is reversible`() {
		val sut = jackson()
		val data = RootElementWithChildren(
			mutableListOf(
				RootElementWithChildren.Element1("Text 1-1"),
				RootElementWithChildren.Element1("Text 1-2")
			),
			mutableListOf(
				RootElementWithChildren.Element2("Text 2-1"),
				RootElementWithChildren.Element2("Text 2-2")
			)
		)
		@Language("xml")
		val xml = """
			<parent>
			  <element1s>
			    <element1>
			      <content>Text 1-1</content>
			    </element1>
			    <element1>
			      <content>Text 1-2</content>
			    </element1>
			  </element1s>
			  <element2s>
			    <element2>
			      <content>Text 2-1</content>
			    </element2>
			    <element2>
			      <content>Text 2-2</content>
			    </element2>
			  </element2s>
			</parent>
		""".trimIndent()

		testSerialization(sut, data, xml)
	}

	// https://github.com/FasterXML/jackson-module-kotlin/issues/153#issuecomment-525304875
	// https://github.com/FasterXML/jackson-module-kotlin/issues/153#issuecomment-692011574
	@JacksonXmlRootElement(localName = "parent")
	data class RootElementWithChildrenWorkaround(
		@Suppress("ConstructorParameterNaming")
		private var _element1s: List<Element1> = emptyList(),

		@Suppress("ConstructorParameterNaming")
		private var _element2s: List<Element2> = emptyList(),
	) {

		@get:JacksonXmlElementWrapper(localName = "element1s")
		@get:JacksonXmlProperty(localName = "element1")
		@get:JsonProperty(index = 1)
		var element1s: List<Element1>
			get() = _element1s
			private set(value) {
				_element1s = value
			}

		@get:JacksonXmlElementWrapper(localName = "element2s")
		@get:JacksonXmlProperty(localName = "element2")
		@get:JsonProperty(index = 2)
		var element2s: List<Element2> 
			get() = _element2s
			private set(value) {
				_element2s = value
			}

		data class Element1(
			val content: String
		)

		data class Element2(
			val content: String
		)
	}

	// TODEL https://github.com/FasterXML/jackson-module-kotlin/issues/153
	@Test fun `RootElementWithChildrenWorkaround serialization is reversible`() {
		val sut = jackson()
		val data = RootElementWithChildrenWorkaround(
			mutableListOf(
				RootElementWithChildrenWorkaround.Element1("Text 1-1"),
				RootElementWithChildrenWorkaround.Element1("Text 1-2")
			),
			mutableListOf(
				RootElementWithChildrenWorkaround.Element2("Text 2-1"),
				RootElementWithChildrenWorkaround.Element2("Text 2-2")
			)
		)

		@Language("xml")
		val xml = """
			<parent>
			  <element1s>
			    <element1>
			      <content>Text 1-1</content>
			    </element1>
			    <element1>
			      <content>Text 1-2</content>
			    </element1>
			  </element1s>
			  <element2s>
			    <element2>
			      <content>Text 2-1</content>
			    </element2>
			    <element2>
			      <content>Text 2-2</content>
			    </element2>
			  </element2s>
			</parent>
		""".trimIndent()

		testSerialization(sut, data, xml)
	}

	@JacksonXmlRootElement(localName = "ContentAndAttributes")
	data class ContentAndAttributes(
		@JacksonXmlProperty(isAttribute = true)
		val attr1: String,
		@JacksonXmlProperty(localName = "innerText")
		@JacksonXmlText
		val content: String,
		@JacksonXmlProperty(isAttribute = true)
		val attr2: Int
	)

	@Test fun `ContentAndAttributes serialization is reversible`() {
		val sut = jackson(configureModule = { setXMLTextElementName("innerText") })
		val data = ContentAndAttributes(
			attr1 = "Attribute 1",
			attr2 = 2,
			content = "Text Content"
		)
		@Language("xml")
		val xml = """
			<ContentAndAttributes attr1="Attribute 1" attr2="2">Text Content</ContentAndAttributes>
		""".trimIndent()

		testSerialization(sut, data, xml)
	}

	data class FormattedDate(
		/**
		 * @sample `"2018-05-16T00:00:00Z"`
		 */
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T00:00:00Z'")
		val someLocalDate: LocalDate
	)

	@Test fun `FormattedDate serialization is reversible`() {
		val sut = jackson()
		val data = FormattedDate(
			someLocalDate = LocalDate.parse("1986-07-01")
		)
		@Language("xml")
		val xml = """
			<FormattedDate>
			  <someLocalDate>1986-07-01T00:00:00Z</someLocalDate>
			</FormattedDate>
		""".trimIndent()

		testSerialization(sut, data, xml)
	}

	data class JustTime(
		/**
		 * @sample `"2018-07-21T10:00:00Z"`
		 */
		val someOffsetDateTime: OffsetDateTime
	)

	@Test fun `JustTime serialization is reversible`() {
		val sut = jackson {
			disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
		}
		val data = JustTime(
			someOffsetDateTime = OffsetDateTime.parse("1986-07-01T10:26:53Z")
		)
		@Language("xml")
		val xml = """
			<JustTime>
			  <someOffsetDateTime>1986-07-01T10:26:53Z</someOffsetDateTime>
			</JustTime>
		""".trimIndent()

		testSerialization(sut, data, xml)
	}

	data class Link(
		val someUrl: URI
	)

	@Test fun `Link serialization is reversible`() {
		val sut = jackson()
		val data = Link(
			someUrl = URI.create("https://www.google.com/imghp?q=pictures")
		)
		@Language("xml")
		val xml = """
			<Link>
			  <someUrl>https://www.google.com/imghp?q=pictures</someUrl>
			</Link>
		""".trimIndent()

		testSerialization(sut, data, xml)
	}
}

private fun jackson(
	configureModule: JacksonXmlModule.() -> Unit = {},
	configure: XmlMapper.() -> Unit = {}
): XmlMapper =
	XmlMapper(JacksonXmlModule().apply(configureModule)).apply {
		registerModule(KotlinModule.Builder().build())
		registerModule(JavaTimeModule())
		enable(SerializationFeature.INDENT_OUTPUT)
		configure()
	}

private fun Any?.short(): String =
	this.toString().replace("[\r\n]".toRegex(), "")

@OptIn(InternalAPI::class)
private inline fun <reified T : Any> testSerialization(sut: XmlMapper, expectedData: T, expectedXml: String): T {
	assertAll {
		o {
			val actualXml: String = try {
				sut.writeValueAsString(expectedData)
			} catch (e: Throwable) {
				fail("Cannot serialize data to XML\n\t\t${expectedData.short()}\n\t\t${e.rootCause!!.message}", e)
			}
			assertEquals(expectedXml.cleanForComparison(), actualXml.cleanForComparison()) {
				"Serialized XML doesn't match, input data:\n\t\t${expectedData.short()}"
			}
		}
		o {
			val actualData: T = try {
				sut.readValue(expectedXml)
			} catch (e: Throwable) {
				fail("Cannot deserialize XML to data:\n\t\t${expectedXml.short()}\n\t\t${e.rootCause!!.message}", e)
			}
			assertEquals(expectedData, actualData) {
				"Deserialized data doesn't match, input XML:\n\t\t${expectedXml.short()}"
			}
		}
		o {
			val actualXml: String = sut.writeValueAsString(expectedData)
			val reRead: T = try {
				sut.readValue(actualXml)
			} catch (e: Throwable) {
				fail("Cannot deserialize serialized XML:\n\t\t${actualXml.short()}\n\t\t${e.rootCause!!.message}", e)
			}
			assertEquals(expectedData, reRead) {
				"Re-read data don't match, input data:\n\t\t${expectedData.short()}\n\t\t->\n\t\t${actualXml.short()}"
			}
		}
	}

	return sut.readValue(expectedXml)
}

private fun String.cleanForComparison(): String =
	trim().replace(Regex("""\r\n?"""), "\n")
