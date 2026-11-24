package vadimerenkov.krona.core.presentation.statistics

sealed interface StatisticsAction {
	data object OnPieChartClick: StatisticsAction
}