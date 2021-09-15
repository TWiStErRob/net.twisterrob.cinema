package net.twisterrob.test

import com.flextrade.jfixture.customisation.Customisation
import java.net.URI

fun javaURIRealistic(): Customisation =
	Customisation { fixture ->
		fixture.customise().lazyInstance(URI::class.java) {
			val protocol: Char = 'a' + fixture.create().inRange(Int::class.java, 0, 'z' - 'a')
			val host: String = fixture.build()
			val path: String = fixture.build()
			val queryKey: String = fixture.build()
			val queryValue: String = fixture.build()
			URI.create("${protocol}://${host}/${path}?${queryKey}=${queryValue}")
		}
	}
