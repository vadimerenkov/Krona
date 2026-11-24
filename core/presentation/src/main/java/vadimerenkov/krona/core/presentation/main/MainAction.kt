package vadimerenkov.krona.core.presentation.main

sealed interface MainAction {
	data class ChangePage(val page: MainPage): MainAction
	data object AddAccount: MainAction
	data object AddTransaction: MainAction
	data object AddCategory: MainAction
	data class OnAccountClick(val id: Long): MainAction
	data class OnTransactionClick(val id: Long): MainAction
	data object OnCategoriesClick: MainAction
	data object OnDebtsClick: MainAction
	data object OnImportClick: MainAction
	data object OnSettingsClick: MainAction
	data object OnPieChartClick: MainAction
}