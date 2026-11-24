package vadimerenkov.krona.core.presentation.account

sealed interface AccountEvent {
	data object FinishedSaving: AccountEvent
}