package vadimerenkov.krona.core.presentation.edit_account

sealed interface EditAccountEvent {
	data object SavingFinished: EditAccountEvent
}