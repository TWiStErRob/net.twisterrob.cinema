package org.junit

/**
 * Binary compatible version for shazamcrest.
 */
class ComparisonFailure(
	message: String?,
	val expected: String,
	val actual: String
) : AssertionError(message)
