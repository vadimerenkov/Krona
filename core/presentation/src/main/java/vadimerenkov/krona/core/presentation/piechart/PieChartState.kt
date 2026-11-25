package vadimerenkov.krona.core.presentation.piechart

import vadimerenkov.krona.core.domain.Category
import vadimerenkov.krona.core.domain.Transaction

data class PieChartState(
	val statsForMonth: Map<Category?, Double> = mapOf(null to 0.0),
	val categoriesBackStack: List<Long?> = emptyList(),
	val isLoading: Boolean = true,
	val transactions: List<Transaction> = emptyList(),
	val type: StatType = StatType.EXPENSE
)

enum class StatType {
	EXPENSE,
	INCOME
}
