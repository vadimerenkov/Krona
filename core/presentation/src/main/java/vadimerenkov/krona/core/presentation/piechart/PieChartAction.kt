package vadimerenkov.krona.core.presentation.piechart

sealed interface PieChartAction {
	data class OnCategoryClick(val categoryId: Long?): PieChartAction
	data object OnBackClick: PieChartAction
	data class ChangeType(val type: StatType): PieChartAction
}