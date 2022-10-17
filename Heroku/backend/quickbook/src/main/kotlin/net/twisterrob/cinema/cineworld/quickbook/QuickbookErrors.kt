package net.twisterrob.cinema.cineworld.quickbook

internal interface QuickbookErrors {

	val errors: List<String>?
}

internal fun <T : QuickbookErrors, R> T.throwErrorOrReturn(block: (T) -> R): R {
	val errors = this.errors
	if (!errors.isNullOrEmpty()) throw QuickbookErrorsException(errors)
	return block(this)
}
