package net.twisterrob.cinema.cineworld.quickbook

import net.twisterrob.test.assertAll
import net.twisterrob.test.that
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class QuickbookErrorsTest {

	private class FakeResponse(
		val data: String,
		override val errors: List<String>?
	) : QuickbookErrors

	@Test fun `empty error list results in data`() {
		val response = FakeResponse("test data", emptyList())

		val result: String = response.throwErrorOrReturn { it.data }

		assertThat(result, equalTo(response.data))
	}

	@Test fun `no error list results in data`() {
		val response = FakeResponse("test data", null)

		val result: String = response.throwErrorOrReturn { it.data }

		assertThat(result, equalTo(response.data))
	}

	@Test fun `one error results in error`() {
		val response = FakeResponse("test data", listOf("fake error"))

		assertThrows<QuickbookErrorsException> { response.throwErrorOrReturn { it.data } }.let { result ->
			assertAll {
				that("errors", result.errors, equalTo(response.errors))
				that("message", result.message, equalTo(response.errors!!.single()))
				that("cause", result.cause, nullValue())
			}
		}
	}

	@Test fun `many error results in error`() {
		val response = FakeResponse("test data", listOf("fake error 1", "fake error 2", "fake error 3"))

		assertThrows<QuickbookErrorsException> { response.throwErrorOrReturn { it.data } }.let { result ->
			assertAll {
				that("errors", result.errors, equalTo(response.errors))
				response.errors!!.forEachIndexed { index, error ->
					that("message $index", result.message, containsString(error))
				}
				that("cause", result.cause, nullValue())
			}
		}
	}
}
