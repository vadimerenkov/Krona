package vadimerenkov.krona.core.presentation.edit_category

import androidx.compose.ui.graphics.Color
import vadimerenkov.krona.core.domain.Category

sealed interface EditCategoryAction {
	data class ChangeParentCategory(val category: Category?): EditCategoryAction
	data class ChangeOnDeleteCategory(val category: Category?): EditCategoryAction
	data object OnSaveClick: EditCategoryAction
	data object DeleteConfirmed: EditCategoryAction
	data class ChangeColor(val color: Color): EditCategoryAction
}