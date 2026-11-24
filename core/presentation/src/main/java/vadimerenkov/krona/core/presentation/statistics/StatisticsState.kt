package vadimerenkov.krona.core.presentation.statistics

import vadimerenkov.krona.core.domain.Category

data class StatisticsState(
	val statsForMonth: Map<Category?, Double> = mapOf(null to 0.0),
)
