package net.twisterrob.cinema.frontend.test.framework

import org.junit.jupiter.api.extension.ExtendWith

@Suppress("UnnecessaryAbstractClass") // Ensure it cannot be instantiated on its own.
@ExtendWith(BrowserExtension::class)
abstract class BaseAngularUiTest
