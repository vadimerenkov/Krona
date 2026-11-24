package vadimerenkov.krona.core.presentation.transactions_list

sealed interface TransactionsListAction {
	data class OnTransactionClick(val id: Long): TransactionsListAction
}