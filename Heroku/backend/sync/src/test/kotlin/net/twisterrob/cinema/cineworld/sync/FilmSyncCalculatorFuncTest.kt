package net.twisterrob.cinema.cineworld.sync

import com.flextrade.jfixture.JFixture
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
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
import net.twisterrob.cinema.cineworld.sync.syndication.Feed.Film as FeedFilm
import net.twisterrob.cinema.database.model.Film as DBFilm

@TagFunctional
class FilmSyncCalculatorFuncTest {

	private val fixture = JFixture().applyCustomisation {
		add(validDBData())
	}
	private val mockCreator: Creator<FeedFilm, DBFilm> = mock()
	private val mockUpdater: Updater<DBFilm, FeedFilm> = mock()
	private lateinit var sut: FilmSyncCalculator

	@BeforeEach fun setUp() {
		sut = FilmSyncCalculator(NodeSyncer(mockCreator, mockUpdater))
	}

	@Test fun `no films in feed result in no data synced`() {
		val feed = Feed(emptyList(), emptyList(), emptyList(), emptyList())
		val db = emptyList<DBFilm>()
		whenever(mockCreator.invoke(any(), eq(feed))).thenAnswer { DBFilm() }

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

	@Test fun `new films in feed result in added films`() {
		val fixtFilm: FeedFilm = fixture.build()
		val feed = Feed(emptyList(), emptyList(), listOf(fixtFilm), emptyList())
		val db = emptyList<DBFilm>()
		whenever(mockCreator.invoke(any(), eq(feed))).thenAnswer { DBFilm() }

		val result = sut.calculate(OffsetDateTime.now(), feed, db)

		assertAll {
			that("insert", result.insert, hasSize(1))
			that("update", result.update, empty())
			that("delete", result.delete, empty())
			that("alreadyDeleted", result.alreadyDeleted, empty())
			that("restore", result.restore, empty())
			o { verifyZeroInteractions(mockUpdater) }
			o { verify(mockCreator).invoke(fixtFilm, feed); verifyNoMoreInteractions(mockCreator) }
		}
	}

	@Test fun `existing films missing from feed result in deleted films`() {
		val feed = Feed(emptyList(), emptyList(), emptyList(), emptyList())
		val fixtDbFilm = fixture.build<DBFilm>().apply {
			_deleted = null
		}
		val db = listOf(fixtDbFilm)
		whenever(mockCreator.invoke(any(), eq(feed))).thenAnswer { DBFilm() }

		val result = sut.calculate(OffsetDateTime.now(), feed, db)

		assertAll {
			that("insert", result.insert, empty())
			that("update", result.update, empty())
			that("delete", result.delete, contains(fixtDbFilm))
			that("alreadyDeleted", result.alreadyDeleted, empty())
			that("restore", result.restore, empty())
			o { verifyZeroInteractions(mockCreator, mockUpdater) }
		}
	}

	@Test fun `existing deleted films missing from feed result in already deleted films`() {
		val feed = Feed(emptyList(), emptyList(), emptyList(), emptyList())
		val fixtDbFilm = fixture.build<DBFilm>().apply {
			_deleted = fixture.build()
		}
		val db = listOf(fixtDbFilm)
		whenever(mockCreator.invoke(any(), eq(feed))).thenAnswer { DBFilm() }

		val result = sut.calculate(OffsetDateTime.now(), feed, db)

		assertAll {
			that("insert", result.insert, empty())
			that("update", result.update, empty())
			that("delete", result.delete, empty())
			that("alreadyDeleted", result.alreadyDeleted, contains(fixtDbFilm))
			that("restore", result.restore, empty())
			o { verifyZeroInteractions(mockCreator, mockUpdater) }
		}
	}

	@Test fun `existing film with matching id is updated`() {
		val fixtFilm: FeedFilm = fixture.build()
		val feed = Feed(emptyList(), emptyList(), listOf(fixtFilm), emptyList())
		val fixtDbFilm = fixture.build<DBFilm>().apply {
			edi = fixtFilm.id
			_deleted = null
		}
		val db = listOf(fixtDbFilm)
		whenever(mockCreator.invoke(any(), eq(feed))).thenAnswer { DBFilm() }

		val result = sut.calculate(OffsetDateTime.now(), feed, db)

		assertAll {
			that("insert", result.insert, empty())
			that("update", result.update, contains(fixtDbFilm))
			that("delete", result.delete, empty())
			that("alreadyDeleted", result.alreadyDeleted, empty())
			that("restore", result.restore, empty())
			o { verifyZeroInteractions(mockCreator) }
			o { verify(mockUpdater).invoke(fixtDbFilm, fixtFilm, feed); verifyNoMoreInteractions(mockUpdater) }
		}
	}

	@Test fun `existing deleted film with matching id is restored`() {
		val fixtFilm: FeedFilm = fixture.build()
		val feed = Feed(emptyList(), emptyList(), listOf(fixtFilm), emptyList())
		val fixtDbFilm = fixture.build<DBFilm>().apply {
			edi = fixtFilm.id
			_deleted = fixture.build()
		}
		val db = listOf(fixtDbFilm)
		whenever(mockCreator.invoke(any(), eq(feed))).thenAnswer { DBFilm() }

		val result = sut.calculate(OffsetDateTime.now(), feed, db)

		assertAll {
			that("insert", result.insert, empty())
			that("update", result.update, empty())
			that("delete", result.delete, empty())
			that("alreadyDeleted", result.alreadyDeleted, empty())
			that("restore", result.restore, contains(fixtDbFilm))
			o { verifyZeroInteractions(mockCreator) }
			o { verify(mockUpdater).invoke(fixtDbFilm, fixtFilm, feed); verifyNoMoreInteractions(mockUpdater) }
		}
	}
}
