package vadimerenkov.krona.core.presentation.import

sealed interface ImportEvent {
	data object ImportFinished: ImportEvent
}