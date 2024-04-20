package net.twisterrob.cinema.frontend.test.framework

import org.intellij.lang.annotations.Language
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver

fun <T> Browser.executeScript(@Language("javascript") script: String, vararg args: Any): T =
	this.driver.executeScript(script, *args)

@Suppress("UNCHECKED_CAST")
fun <T> WebDriver.executeScript(@Language("javascript") script: String, vararg args: Any): T =
	(this as JavascriptExecutor).executeScript(script, *args) as T

fun <T> Browser.executeAsyncScript(@Language("javascript") script: String, vararg args: Any): T =
	this.driver.executeAsyncScript(script, *args)

@Suppress("UNCHECKED_CAST")
fun <T> WebDriver.executeAsyncScript(@Language("javascript") script: String, vararg args: Any): T =
	(this as JavascriptExecutor).executeAsyncScript(script, *args) as T
