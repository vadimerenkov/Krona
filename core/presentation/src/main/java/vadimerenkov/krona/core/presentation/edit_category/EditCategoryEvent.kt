package vadimerenkov.krona.core.presentation.edit_category

sealed interface EditCategoryEvent {
	data object FinishedSaving: EditCategoryEvent
}