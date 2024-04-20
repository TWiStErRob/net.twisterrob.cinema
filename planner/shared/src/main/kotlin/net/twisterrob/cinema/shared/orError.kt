package net.twisterrob.cinema.shared

fun <T : Any?> T?.orError(message: String): T & Any =
	this ?: error(message)

// See https://stackoverflow.com/a/76324369/253468
@Deprecated(
	message = "orError is only applicable to nullable receivers.",
	level = DeprecationLevel.ERROR,
	replaceWith = ReplaceWith("")
)
fun <T : Any> @Suppress("UnusedReceiverParameter") T.orError(@Suppress("UNUSED_PARAMETER") message: String): Nothing =
	error("This should never be called.")
