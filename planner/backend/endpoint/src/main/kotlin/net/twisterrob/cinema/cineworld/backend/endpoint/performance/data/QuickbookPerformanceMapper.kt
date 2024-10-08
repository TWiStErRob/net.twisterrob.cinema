package net.twisterrob.cinema.cineworld.backend.endpoint.performance.data

import net.twisterrob.cinema.cineworld.quickbook.QuickbookPerformance
import org.jetbrains.annotations.TestOnly
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZoneId
import java.time.ZoneOffset
import javax.inject.Inject

class QuickbookPerformanceMapper @Inject constructor() {

	fun map(date: LocalDate, cinema: Long, film: Long, qbPerformances: List<QuickbookPerformance>): Performances {
		val performances: List<Performances.Performance> = qbPerformances.map { performance ->
			Performances.Performance(
				time = getOffsetTime(date, performance.time),
				isAvailable = performance.isAvailable,
				bookingUrl = performance.bookingUrl,
				type = performance.type,
				isAudioDescribed = performance.isAudioDescribed,
				isSuperScreen = performance.isSuperScreen,
				isSubtitled = performance.isSubtitled
			)
		}
		val utcMidnight = date.atTime(OffsetTime.of(LocalTime.MIDNIGHT, ZoneOffset.UTC))
		return Performances(
			date = utcMidnight,
			cinema = cinema,
			film = film,
			performances = performances,
		)
	}

	@TestOnly internal fun getOffsetTime(date: LocalDate, time: LocalTime): OffsetDateTime {
		val combinedTime = date.atTime(time)
		val offset = LONDON.rules.getOffset(combinedTime)
		return combinedTime.atOffset(offset).withOffsetSameInstant(ZoneOffset.UTC)
	}

	companion object {

		/**
		 * Assume all the cinemas to be in the UK.
		 * This will get fun when Ireland has different DST rules.
		 */
		private val LONDON = ZoneId.of("Europe/London")
	}
}
