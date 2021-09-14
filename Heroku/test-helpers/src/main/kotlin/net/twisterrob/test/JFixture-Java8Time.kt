package net.twisterrob.test

import com.flextrade.jfixture.customisation.Customisation
import java.time.Clock
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

private val TEN_YEARS = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS) * 365 * 10

fun java8TimeRealistic(): Customisation =
	Customisation { fixture ->
		offsetDateTimeRealistic().customise(fixture)
		zonedDateTimeRealistic().customise(fixture)
	}

/**
 * @param minOffset 0 to make time in future only
 * @param maxOffset 0 to make time in past only
 */
fun offsetDateTimeRealistic(minOffset: Long = TEN_YEARS, maxOffset: Long = TEN_YEARS): Customisation =
	Customisation { fixture ->
		fixture.customise().lazyInstance(OffsetDateTime::class.java) {
			val now = System.currentTimeMillis()
			val millis = fixture.buildRange((now - minOffset)..(now + maxOffset))
			val zone = fixture.create().fromList(*ZoneId.getAvailableZoneIds().toTypedArray())
			OffsetDateTime.now(Clock.fixed(Instant.ofEpochMilli(millis), ZoneId.of(zone)))
		}
	}

/**
 * @param minOffset 0 to make time in future only
 * @param maxOffset 0 to make time in past only
 */
fun zonedDateTimeRealistic(minOffset: Long = TEN_YEARS, maxOffset: Long = TEN_YEARS): Customisation =
	Customisation { fixture ->
		fixture.customise().lazyInstance(ZonedDateTime::class.java) {
			val now = System.currentTimeMillis()
			val millis = fixture.buildRange((now - minOffset)..(now + maxOffset))
			val zone = fixture.create().fromList(*ZoneId.getAvailableZoneIds().toTypedArray())
			ZonedDateTime.now(Clock.fixed(Instant.ofEpochMilli(millis), ZoneId.of(zone)))
		}
	}
