package net.twisterrob.cinema.cineworld.sync

import com.flextrade.jfixture.JFixture
import net.twisterrob.cinema.database.model.Historical
import net.twisterrob.test.applyCustomisation
import net.twisterrob.test.build
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@Suppress("ClassName", "RemoveExplicitTypeArguments")
class SyncResultsTest {

	private class Data : Historical()

	private val fixture = JFixture()

	@Test fun `empty results is valid`() {
		SyncResults<Data>(
			insert = emptyList(),
			update = emptyList(),
			restore = emptyList(),
			delete = emptyList(),
			alreadyDeleted = emptyList()
		).validate()
	}

	@Test fun `customized fixture is valid`() {
		fixture.applyCustomisation { add(syncResults<Data>()) }
		val syncResults: SyncResults<Data> = fixture.build()

		syncResults.validate()
	}

	@Test fun `uncustomized fixture is invalid`() {
		val result = assertThrows<UnsupportedOperationException> {
			val syncResults: SyncResults<Data> = fixture.build()
			syncResults.validate()
		}

		assertThat(
			result.message!!.lines().first(),
			equalTo("JFixture was unable to create an instance of java.lang.reflect.TypeVariable")
		)
	}

	@Nested
	inner class Insert {

		@Test fun valid() {
			SyncResults<Data>(
				insert = listOf(fixture.buildValidInsert()),
				update = emptyList(),
				restore = emptyList(),
				delete = emptyList(),
				alreadyDeleted = emptyList()
			).validate()
		}

		@Test fun `a set _deleted is not valid`() {
			assertThrows<IllegalStateException> {
				SyncResults<Data>(
					insert = listOf(fixture.buildValidInsert<Data>().apply { _deleted = fixture.build() }),
					update = emptyList(),
					restore = emptyList(),
					delete = emptyList(),
					alreadyDeleted = emptyList()
				).validate()
			}.let { ex -> assertThat(ex.message, containsString("should not be deleted")) }
		}
	}

	@Nested
	inner class Update {

		@Test fun valid() {
			SyncResults<Data>(
				insert = emptyList(),
				update = listOf(fixture.buildValidUpdate()),
				restore = emptyList(),
				delete = emptyList(),
				alreadyDeleted = emptyList()
			).validate()
		}

		@Test fun `a set _deleted is not valid`() {
			assertThrows<IllegalStateException> {
				SyncResults<Data>(
					insert = emptyList(),
					update = listOf(fixture.buildValidUpdate<Data>().apply { _deleted = fixture.build() }),
					restore = emptyList(),
					delete = emptyList(),
					alreadyDeleted = emptyList()
				).validate()
			}.let { ex -> assertThat(ex.message, containsString("should not be deleted")) }
		}
	}

	@Nested
	inner class Restore {

		@Test fun valid() {
			SyncResults<Data>(
				insert = emptyList(),
				update = emptyList(),
				restore = listOf(fixture.buildValidRestore()),
				delete = emptyList(),
				alreadyDeleted = emptyList()
			).validate()
		}

		@Test fun `a set _deleted is not valid`() {
			assertThrows<IllegalStateException> {
				SyncResults<Data>(
					insert = emptyList(),
					update = emptyList(),
					restore = listOf(fixture.buildValidRestore<Data>().apply { _deleted = fixture.build() }),
					delete = emptyList(),
					alreadyDeleted = emptyList()
				).validate()
			}.let { ex -> assertThat(ex.message, containsString("should not be deleted")) }
		}
	}

	@Nested
	inner class Delete {

		@Test fun valid() {
			SyncResults<Data>(
				insert = emptyList(),
				update = emptyList(),
				restore = emptyList(),
				delete = listOf(fixture.buildValidDelete()),
				alreadyDeleted = emptyList()
			).validate()
		}

		@Test fun `a missing _deleted is not valid`() {
			assertThrows<IllegalStateException> {
				SyncResults<Data>(
					insert = emptyList(),
					update = emptyList(),
					restore = emptyList(),
					delete = listOf(fixture.buildValidDelete<Data>().apply { _deleted = null }),
					alreadyDeleted = emptyList()
				).validate()
			}.let { ex -> assertThat(ex.message, containsString("should be deleted")) }
		}
	}

	@Nested
	inner class AlreadyDeleted {

		@Test fun valid() {
			SyncResults<Data>(
				insert = emptyList(),
				update = emptyList(),
				restore = emptyList(),
				delete = emptyList(),
				alreadyDeleted = listOf(fixture.buildValidAlreadyDeleted())
			).validate()
		}

		@Test fun `a missing _deleted is not valid`() {
			assertThrows<IllegalStateException> {
				SyncResults<Data>(
					insert = emptyList(),
					update = emptyList(),
					restore = emptyList(),
					delete = emptyList(),
					alreadyDeleted = listOf(fixture.buildValidAlreadyDeleted<Data>().apply { _deleted = null })
				).validate()
			}.let { ex -> assertThat(ex.message, containsString("should be deleted")) }
		}
	}
}

private inline fun <reified T : Historical> JFixture.buildValidInsert(): T =
	this.build<T>().apply {
		_created = build()
		_updated = null
		_deleted = null
	}

private inline fun <reified T : Historical> JFixture.buildValidUpdate(): T =
	this.build<T>().apply {
		_created = build()
		_updated = build()
		_deleted = null
	}

private inline fun <reified T : Historical> JFixture.buildValidRestore(): T =
	this.build<T>().apply {
		_created = build()
		_updated = if (build()) build() else null
		_deleted = null
	}

private inline fun <reified T : Historical> JFixture.buildValidDelete(): T =
	this.build<T>().apply {
		_created = build()
		_updated = if (build()) build() else null
		_deleted = build()
	}

private inline fun <reified T : Historical> JFixture.buildValidAlreadyDeleted(): T =
	this.build<T>().apply {
		_created = build()
		_updated = if (build()) build() else null
		_deleted = build()
	}
