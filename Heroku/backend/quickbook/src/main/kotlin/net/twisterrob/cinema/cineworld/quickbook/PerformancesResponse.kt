package net.twisterrob.cinema.cineworld.quickbook

internal class PerformancesResponse(
	val performances: List<QuickbookPerformance>,
	val legend: List<PerformancesLegend>,
	override val errors: List<String>?
) : QuickbookErrors
