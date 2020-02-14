package net.twisterrob.cinema.cineworld.sync

import com.flextrade.jfixture.JFixture
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import net.twisterrob.cinema.cineworld.sync.syndication.Feed
import net.twisterrob.cinema.database.model.validDBData
import net.twisterrob.test.TagFunctional
import net.twisterrob.test.applyCustomisation
import net.twisterrob.test.assertAll
import net.twisterrob.test.build
import net.twisterrob.test.that
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import net.twisterrob.cinema.cineworld.sync.syndication.Feed.Cinema as FeedCinema
import net.twisterrob.cinema.database.model.Cinema as DBCinema

@TagFunctional
class CinemaSyncCalculatorFuncTest {

	private val fixture = JFixture().applyCustomisation {
		add(validDBData())
	}
	private val mockCreator: Creator<FeedCinema, DBCinema> = mock()
	private val mockUpdater: Updater<DBCinema, FeedCinema> = mock()
	private lateinit var sut: CinemaSyncCalculator

	@BeforeEach fun setUp() {
		sut = CinemaSyncCalculator(NodeSyncer(mockCreator, mockUpdater))
		whenever(mockCreator.invoke(any())).thenAnswer { DBCinema() }
	}

	@Test fun `no cinemas in feed result in no data synced`() {
		val feed = Feed(emptyList(), emptyList(), emptyList(), emptyList())
		val db = emptyList<DBCinema>()

		val result = sut.calculate(OffsetDateTime.now(), feed, db)

		assertAll {
			that("insert", result.insert, empty())
			that("update", result.update, empty())
			that("delete", result.delete, empty())
			that("alreadyDeleted", result.alreadyDeleted, empty())
			that("restore", result.restore, empty())
			o { verifyZeroInteractions(mockCreator, mockUpdater) }
		}
	}

	@Test fun `new cinemas in feed result in added cinemas`() {
		val fixtCinema: FeedCinema = fixture.build()
		val feed = Feed(emptyList(), listOf(fixtCinema), emptyList(), emptyList())
		val db = emptyList<DBCinema>()

		val result = sut.calculate(OffsetDateTime.now(), feed, db)

		assertAll {
			that("insert", result.insert, hasSize(1))
			that("update", result.update, empty())
			that("delete", result.delete, empty())
			that("alreadyDeleted", result.alreadyDeleted, empty())
			that("restore", result.restore, empty())
			o { verifyZeroInteractions(mockUpdater) }
			o { verify(mockCreator).invoke(fixtCinema); verifyNoMoreInteractions(mockCreator) }
		}
	}

	@Test fun `existing cinemas missing from feed result in deleted cinemas`() {
		val feed = Feed(emptyList(), emptyList(), emptyList(), emptyList())
		val fixtDbCinema = fixture.build<DBCinema>().apply {
			_deleted = null
		}
		val db = listOf(fixtDbCinema)

		val result = sut.calculate(OffsetDateTime.now(), feed, db)

		assertAll {
			that("insert", result.insert, empty())
			that("update", result.update, empty())
			that("delete", result.delete, contains(fixtDbCinema))
			that("alreadyDeleted", result.alreadyDeleted, empty())
			that("restore", result.restore, empty())
			o { verifyZeroInteractions(mockCreator, mockUpdater) }
		}
	}

	@Test fun `existing deleted cinemas missing from feed result in already deleted cinemas`() {
		val feed = Feed(emptyList(), emptyList(), emptyList(), emptyList())
		val fixtDbCinema = fixture.build<DBCinema>().apply {
			_deleted = fixture.build()
		}
		val db = listOf(fixtDbCinema)

		val result = sut.calculate(OffsetDateTime.now(), feed, db)

		assertAll {
			that("insert", result.insert, empty())
			that("update", result.update, empty())
			that("delete", result.delete, empty())
			that("alreadyDeleted", result.alreadyDeleted, contains(fixtDbCinema))
			that("restore", result.restore, empty())
			o { verifyZeroInteractions(mockCreator, mockUpdater) }
		}
	}

	@Test fun `existing cinema with matching id is updated`() {
		val fixtCinema: FeedCinema = fixture.build()
		val feed = Feed(emptyList(), listOf(fixtCinema), emptyList(), emptyList())
		val fixtDbCinema = fixture.build<DBCinema>().apply {
			cineworldID = fixtCinema.id
			_deleted = null
		}
		val db = listOf(fixtDbCinema)

		val result = sut.calculate(OffsetDateTime.now(), feed, db)

		assertAll {
			that("insert", result.insert, empty())
			that("update", result.update, contains(fixtDbCinema))
			that("delete", result.delete, empty())
			that("alreadyDeleted", result.alreadyDeleted, empty())
			that("restore", result.restore, empty())
			o { verifyZeroInteractions(mockCreator) }
			o { verify(mockUpdater).invoke(fixtDbCinema, fixtCinema); verifyNoMoreInteractions(mockUpdater) }
		}
	}

	@Test fun `existing deleted cinema with matching id is restored`() {
		val fixtCinema: FeedCinema = fixture.build()
		val feed = Feed(emptyList(), listOf(fixtCinema), emptyList(), emptyList())
		val fixtDbCinema = fixture.build<DBCinema>().apply {
			cineworldID = fixtCinema.id
			_deleted = fixture.build()
		}
		val db = listOf(fixtDbCinema)

		val result = sut.calculate(OffsetDateTime.now(), feed, db)

		assertAll {
			that("insert", result.insert, empty())
			that("update", result.update, empty())
			that("delete", result.delete, empty())
			that("alreadyDeleted", result.alreadyDeleted, empty())
			that("restore", result.restore, contains(fixtDbCinema))
			o { verifyZeroInteractions(mockCreator) }
			o { verify(mockUpdater).invoke(fixtDbCinema, fixtCinema); verifyNoMoreInteractions(mockUpdater) }
		}
	}
}
