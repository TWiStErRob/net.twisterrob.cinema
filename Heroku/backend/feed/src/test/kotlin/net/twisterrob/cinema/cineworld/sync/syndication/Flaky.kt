@file:Suppress("unused", "UNUSED_VARIABLE")

package net.twisterrob.cinema.cineworld.sync.syndication

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled("Flaky")
class Flaky {

	@JacksonXmlRootElement(localName = "parent")
	data class RootElementWithChildren(
		private var _element1s: MutableList<Element1>,
		private var _element2s: MutableList<Element2>
	) {

		var element1s: MutableList<Element1>
			@JacksonXmlElementWrapper(localName = "element1s")
			@JacksonXmlProperty(localName = "element1")
			get() = _element1s
			set(value) {
				_element1s = value
			}

		var element2s: MutableList<Element2>
			@JacksonXmlElementWrapper(localName = "element2s")
			@JacksonXmlProperty(localName = "element2")
			get() = _element2s
			set(value) {
				_element2s = value
			}

		data class Element1(
			val content: String
		)

		data class Element2(
			val content: String
		)
	}

	@Test fun test() {

		val sut = XmlMapper().apply {
			registerKotlinModule()
			enable(SerializationFeature.INDENT_OUTPUT)
		}
		val data = RootElementWithChildren(
			_element1s = mutableListOf(
				RootElementWithChildren.Element1("Text 1-1"),
				RootElementWithChildren.Element1("Text 1-2")
			),
			_element2s = mutableListOf(
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

		// STOPSHIP report this is flaky because xml element1s/element2s can swap orders in actual.
		assertEquals(xml.cleanForComparison(), sut.writeValueAsString(data).cleanForComparison())
	}

	@JacksonXmlRootElement(localName = "parent")
	data class RootElementWithChildren2(
		private var _element1s: MutableList<Element1>,
		private var _element2s: MutableList<Element2>
	) {

		var element1s: MutableList<Element1>
			@JacksonXmlElementWrapper(localName = "element1s")
			@JacksonXmlProperty(localName = "element1")
			get() = _element1s
			set(value) {
				_element1s = value
			}

		var element2s: MutableList<Element2>
			@JacksonXmlElementWrapper(localName = "element2s")
			@JacksonXmlProperty(localName = "element2")
			get() = _element2s
			set(value) {
				_element2s = value
			}

		data class Element1(
			val content: String
		)

		data class Element2(
			val content: String
		)
	}

	@Test fun `RootElementWithChildren2 remove 2 and will consistently fail`() {
		val sut = XmlMapper().apply {
			registerKotlinModule()
			enable(SerializationFeature.INDENT_OUTPUT)
		}
		val data = RootElementWithChildren(
			_element1s = mutableListOf(
				RootElementWithChildren.Element1("Text 1-1"),
				RootElementWithChildren.Element1("Text 1-2")
			),
			_element2s = mutableListOf(
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

//		testSerialization(sut, data, xml)
	}
}

private fun String.cleanForComparison(): String =
	trim().replace(Regex("""\r\n?"""), "\n")
