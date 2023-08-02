@file:Suppress("UnusedReceiverParameter")

package net.twisterrob.cinema.frontend.test.framework

import com.paulhammant.ngwebdriver.ByAngular
import org.openqa.selenium.By

object Byk

fun Byk.buttonText(text: String): By = ByAngular.buttonText(text) // STOPSHIP inline
fun Byk.repeater(text: String): By = ByAngular.repeater(text) // STOPSHIP inline
