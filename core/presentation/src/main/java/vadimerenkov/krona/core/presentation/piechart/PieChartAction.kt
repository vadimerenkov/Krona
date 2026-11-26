package vadimerenkov.krona.core.presentation.piechart

import java.time.YearMonth

sealed interface PieChartAction {
	data class OnCategoryClick(val categoryId: Long?): PieChartAction
	data object OnBackClick: PieChartAction
	data class ChangeType(val type: StatType): PieChartAction
	data class SelectMonth(val month: YearMonth): PieChartAction
}