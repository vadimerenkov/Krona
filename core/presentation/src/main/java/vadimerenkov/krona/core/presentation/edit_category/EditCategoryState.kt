package vadimerenkov.krona.core.presentation.edit_category

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.graphics.Color
import vadimerenkov.krona.core.domain.Category

data class EditCategoryState(
	val nameState: TextFieldState = TextFieldState(),
	val allCategories: List<Category> = emptyList(),
	val parentCategory: Category? = null,
	val disabledCategories: List<Category> = emptyList(),
	val isDeleteEnabled: Boolean = false,
	val onDeleteCategory: Category? = null,
	val color: Color = Color.Transparent
) {
	val isValid
		get() = nameState.text.isNotBlank()
}