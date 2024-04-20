package net.twisterrob.cinema.cineworld.quickbook

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

internal class PerformancesResponse @JsonCreator constructor(
	@JsonProperty("performances")
	val performances: List<QuickbookPerformance>,

	@JsonProperty("legend")
	val legend: List<PerformancesLegend>,

	@JsonProperty("errors")
	override val errors: List<String>?,
) : QuickbookErrors
