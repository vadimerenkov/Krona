package vadimerenkov.krona.core.presentation.piechart

import vadimerenkov.krona.core.domain.Category
import vadimerenkov.krona.core.domain.Transaction
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime

data class PieChartState(
	val statsForMonth: Map<Category?, Double> = mapOf(null to 0.0),
	val categoriesBackStack: List<Long?> = emptyList(),
	val isLoading: Boolean = true,
	val transactions: List<Transaction> = emptyList(),
	val type: StatType = StatType.EXPENSE,
	val selectedMonth: YearMonth = YearMonth.now()
) {
	val start: ZonedDateTime
		get() = selectedMonth.atDay(1).atStartOfDay().atZone(ZoneId.systemDefault())

	val end: ZonedDateTime
		get() = selectedMonth.atEndOfMonth().atTime(23, 59).atZone(ZoneId.systemDefault())
}

enum class StatType {
	EXPENSE,
	INCOME
}
