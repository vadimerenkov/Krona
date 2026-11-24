package vadimerenkov.krona.core.presentation.accounts_list

sealed interface AccountsListAction {
	data class OnAccountClick(val id: Long): AccountsListAction
	data object OnShowArchivedClick: AccountsListAction
}