package vadimerenkov.krona.core.presentation.transaction

sealed interface TransactionEvent {
	data object SavingFinished: TransactionEvent
}