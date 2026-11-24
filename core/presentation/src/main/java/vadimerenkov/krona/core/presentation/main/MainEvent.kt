package vadimerenkov.krona.core.presentation.main

sealed interface MainEvent {
	data object StartOnboarding: MainEvent
}