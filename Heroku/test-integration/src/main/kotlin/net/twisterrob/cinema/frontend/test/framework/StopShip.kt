package net.twisterrob.cinema.frontend.test.framework

import org.openqa.selenium.By
import org.openqa.selenium.SearchContext
import org.openqa.selenium.WebElement

// STOPSHIP inline these after diffing

typealias ElementArrayFinder = List<WebElement>
typealias ElementFinder = WebElement
//typealias ? = SearchContext

fun SearchContext.element(by: By): WebElement = this.findElement(by)
fun WebElement.element(by: By): WebElement = this.findElement(by)
fun WebElement.all(by: By): List<WebElement> = this.findElements(by)
fun List<WebElement>.all(by: By): List<WebElement> = this.flatMap { it.findElements(by) }
