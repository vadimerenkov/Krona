package vadimerenkov.krona.core.presentation.transaction

sealed interface TransactionAction {
	data object DeleteConfirmed: TransactionAction
	data object OnEditClick: TransactionAction
}