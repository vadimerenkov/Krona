package vadimerenkov.krona.core.presentation.edit_transaction

sealed interface EditTransactionEvent {
	data object SavingFinished: EditTransactionEvent
}