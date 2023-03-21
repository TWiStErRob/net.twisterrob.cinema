package net.twisterrob.test

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@Suppress("unused", "UseDataClass")
class ReflectionKtUnitTest {

	@Test fun `get public readonly field`() {
		class C {
			val publicConst = "value"
		}

		val value: String = C()["publicConst"]

		assertEquals("value", value)
	}

	@Test fun `get private readonly field`() {
		class C {
			private val privateField = "value"
		}

		val value: String = C()["privateField"]

		assertEquals("value", value)
	}

	@Test fun `get public mutable primary property`() {
		class C(
			var publicProp: String
		)

		val value: String = C("value")["publicProp"]

		assertEquals("value", value)
	}

	@Test fun `get private mutable primary property`() {
		class C(
			private var privateProp: String
		)

		val value: String = C("value")["privateProp"]

		assertEquals("value", value)
	}

	@Test fun `set public readonly field`() {
		class C {
			val publicConst = "value"
		}

		val instance = C()
		instance["publicConst"] = "value2"

		assertEquals("value2", instance.publicConst)
	}

	@Test fun `set private readonly field`() {
		class C {
			private val privateField = "value"
		}

		val instance = C()
		instance["privateField"] = "value2"

		assertEquals("value2", instance["privateField"])
	}

	@Test fun `set public mutable primary property`() {
		class C(
			var publicProp: String
		)

		val instance = C("value")
		instance["publicProp"] = "value2"

		assertEquals("value2", instance.publicProp)
	}

	@Test fun `set private mutable primary property`() {
		class C(
			private var privateProp: String
		)

		val instance = C("value")
		instance["privateProp"] = "value2"

		assertEquals("value2", instance["privateProp"])
	}

	@Test fun `get inherted property`() {
		open class S {
			var prop: String = "value"
		}

		class C : S()

		val value: String = C()["prop"]

		assertEquals("value", value)
	}

	@Test fun `set inherited property`() {
		open class S {
			var prop: String = "value"
		}

		class C : S()

		val instance = C()
		instance["prop"] = "value2"

		assertEquals("value2", instance.prop)
	}

	@Test fun `get property with wrong type`() {
		class C(
			var propS: String
		)

		val ex = assertThrows<ClassCastException> {
			@Suppress("UNUSED_VARIABLE") // stored to force inference
			val value: Int = C("value")["propS"]
		}
		assertAll {
			that("mentions the property", ex.message, containsString("\$C.propS"))
			that("mentions the right type", ex.message, containsString("String"))
			that("mentions the wrong type", ex.message, containsString("Int"))
		}
	}

	@Test fun `set property with wrong type`() {
		class C(
			var propS: String
		)

		val ex = assertThrows<ClassCastException> {
			C("value")["propS"] = 42
		}
		assertAll {
			that("mentions the property", ex.message, containsString("\$C.propS"))
			that("mentions the right type", ex.message, containsString("String"))
			that("mentions the wrong type", ex.message, containsString("Int"))
		}
	}

	@Test fun `get missing property`() {
		class C

		val ex = assertThrows<NoSuchFieldException> { C()["prop"] }
		assertThat("mentions the property", ex.message, containsString("'prop'"))
	}

	@Test fun `set missing property`() {
		class C

		val ex = assertThrows<NoSuchFieldException> { C()["prop"] = "value" }
		assertThat("mentions the property", ex.message, containsString("'prop'"))
	}

	@Test fun `get duplicate inherited property`() {
		open class S {
			private val prop: String = "valueS"
		}

		class C : S() {
			private val prop: String = "valueC"
		}

		val ex = assertThrows<NoSuchFieldException> { C()["prop"] }
		assertAll {
			that("mentions the property", ex.message, containsString("'prop'"))
			that("mentions the super occurrence", ex.message, containsString("\$S.prop"))
			that("mentions the child occurrence", ex.message, containsString("\$C.prop"))
		}
	}

	@Test fun `set duplicate inherited property`() {
		open class S {
			@Suppress("VarCouldBeVal")
			private var prop: String = "valueS"
		}

		class C : S() {
			@Suppress("VarCouldBeVal")
			private var prop: String = "valueC"
		}

		val ex = assertThrows<NoSuchFieldException> { C()["prop"] = "value" }
		assertAll {
			that("mentions the property", ex.message, containsString("'prop'"))
			that("mentions the super occurrence", ex.message, containsString("\$S.prop"))
			that("mentions the child occurrence", ex.message, containsString("\$C.prop"))
		}
	}
}
