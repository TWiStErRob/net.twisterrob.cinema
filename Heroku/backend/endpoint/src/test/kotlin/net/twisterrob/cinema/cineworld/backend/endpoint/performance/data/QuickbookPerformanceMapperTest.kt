package net.twisterrob.cinema.cineworld.backend.endpoint.performance.data

import com.flextrade.jfixture.JFixture
import net.twisterrob.cinema.cineworld.quickbook.QuickbookPerformance
import net.twisterrob.test.assertAll
import net.twisterrob.test.build
import net.twisterrob.test.that
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

class QuickbookPerformanceMapperTest {

	private val fixture = JFixture()
	private val sut = QuickbookPerformanceMapper()

	@Test fun `top level fields are mapped correctly`() {
		val fixtDate: LocalDate = fixture.build()
		val fixtCinema: Long = fixture.build()
		val fixtFilm: Long = fixture.build()

		val result = sut.map(fixtDate, fixtCinema, fixtFilm, emptyList())

		assertAll {
			that("cinema", result.cinema, equalTo(fixtCinema))
			that("film", result.film, equalTo(fixtFilm))
			that("date", result.date.toLocalDate(), equalTo(fixtDate))
			that("time", result.date.toLocalTime(), equalTo(LocalTime.MIDNIGHT))
			that("timeZone", result.date.offset, equalTo(ZoneOffset.UTC))
			that("performances", result.performances, empty())
		}
	}

	@Test fun `fields are mapped correctly`() {
		val fixtDate: LocalDate = fixture.build()
		val fixtTime: OffsetDateTime = fixture.build()
		val fixtPerformance: QuickbookPerformance = fixture.build()
		val sut = spy(this.sut)
		doReturn(fixtTime).whenever(sut).getOffsetTime(fixtDate, fixtPerformance.time)

		val result = sut.map(fixtDate, fixture.build(), fixture.build(), listOf(fixtPerformance))

		assertThat(result.performances, hasSize(1))
		result.performances.single().let { performance ->
			assertAll {
				that("time", performance.time, equalTo(fixtTime))
				that("available", performance.available, equalTo(fixtPerformance.available))
				that("booking_url", performance.bookingUrl, equalTo(fixtPerformance.bookingUrl))
				that("type", performance.type, equalTo(fixtPerformance.type))
				that("ad", performance.isAudioDescribed, equalTo(fixtPerformance.isAudioDescribed))
				that("ss", performance.isSuperScreen, equalTo(fixtPerformance.isSuperScreen))
				that("subtitled", performance.isSubtitled, equalTo(fixtPerformance.isSubtitled))
			}
		}
	}

	@Test fun `winter time returns as correct offset`() {
		val winter = LocalDate.of(2020, 12, 21) // winter solstice
		val time = LocalTime.of(10, 2, 0, 0) // GMT(+0)

		val result = sut.getOffsetTime(winter, time)

		assertEquals(OffsetDateTime.of(2020, 12, 21, 10, 2, 0, 0, ZoneOffset.UTC), result)
	}

	@Test fun `summer time returns as correct offset`() {
		val winter = LocalDate.of(2020, 6, 20) // summer solstice
		val time = LocalTime.of(22, 43, 0, 0) // BST(+1)

		val result = sut.getOffsetTime(winter, time)

		assertEquals(OffsetDateTime.of(2020, 6, 20, 21, 43, 0, 0, ZoneOffset.UTC), result)
	}
}
