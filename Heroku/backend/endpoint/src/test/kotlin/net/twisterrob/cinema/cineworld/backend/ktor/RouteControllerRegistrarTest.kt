package net.twisterrob.cinema.cineworld.backend.ktor

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.ktor.application.Application
import io.ktor.routing.Routing
import net.twisterrob.test.KtorExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(KtorExtension::class)
class RouteControllerRegistrarTest {

	private val controllers: MutableSet<RouteController> = LinkedHashSet()

	private lateinit var sut: RouteControllerRegistrar

	@BeforeEach fun setUp(application: Application) {
		sut = RouteControllerRegistrar(
			application,
			controllers
		)
	}

	@Test fun `empty controllers fail`() {
		controllers.addAll(emptyList())

		val result = assertThrows<IllegalArgumentException> {
			sut.register()
		}

		assertEquals("There are no controllers, not starting up.", result.message)
	}

	@Test fun `one controller registers`() {
		val mockController: RouteController = mock()
		controllers.addAll(listOf(mockController))

		sut.register()

		verify(mockController).run { with(any<Routing>()) { registerRoutes() } }
	}

	@Test fun `two controllers register by order`() {
		val mockController1: RouteController = mock()
		whenever(mockController1.order).thenReturn(1)
		val mockController2: RouteController = mock()
		whenever(mockController2.order).thenReturn(2)
		// Swap to make sure LinkedHashSet iterates in the "wrong" order.
		controllers.addAll(listOf(mockController2, mockController1))

		sut.register()

		inOrder(mockController1, mockController2) {
			verify(mockController1).run { with(any<Routing>()) { registerRoutes() } }
			verify(mockController2).run { with(any<Routing>()) { registerRoutes() } }
		}
	}

	@Test fun `two controllers register by name`(application: Application) {
		@Suppress("UnnecessaryAbstractClass") // TODEL False positive https://github.com/detekt/detekt/issues/4753
		abstract class RouteController1 : RouteController(application)
		@Suppress("UnnecessaryAbstractClass") // TODEL False positive https://github.com/detekt/detekt/issues/4753
		abstract class RouteController2 : RouteController(application)

		val mockController1: RouteController1 = mock()
		whenever(mockController1.order).thenReturn(0)
		val mockController2: RouteController2 = mock()
		whenever(mockController2.order).thenReturn(0)
		// Swap to make sure LinkedHashSet iterates in the "wrong" order.
		controllers.addAll(listOf(mockController2, mockController1))

		sut.register()

		inOrder(mockController1, mockController2) {
			verify(mockController1).run { with(any<Routing>()) { registerRoutes() } }
			verify(mockController2).run { with(any<Routing>()) { registerRoutes() } }
		}
	}
}
