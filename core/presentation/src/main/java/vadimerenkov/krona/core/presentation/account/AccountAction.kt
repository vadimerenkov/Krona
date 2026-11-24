package vadimerenkov.krona.core.presentation.account

sealed interface AccountAction {
	data object OnSaveChangeBalanceClick: AccountAction
	data class OnTransactionClick(val id: Long): AccountAction
	data object OnDeleteClick: AccountAction
	data object OnArchiveClick: AccountAction
	data object OnEditClick: AccountAction
}