package vadimerenkov.krona.core.presentation.edit_category

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vadimerenkov.krona.core.domain.Category
import vadimerenkov.krona.core.domain.TransactionsRepository
import vadimerenkov.krona.core.domain.util.flatten

class EditCategoryViewModel(
	val id: Long,
	val repository: TransactionsRepository
): ViewModel() {

	var state by mutableStateOf(EditCategoryState())

	private val channel = Channel<EditCategoryEvent>()
	val events = channel.receiveAsFlow()

	init {
		viewModelScope.launch {
			val categories = repository.getCategories().first()

			val category = if (id != 0L) {
				categories.flatten().find { it.id == id }
			} else null

			state.nameState.edit {
				replace(0, length, category?.name ?: "")
			}

			val parentCategory = category?.parentCategory

			val disabledCategories = mutableListOf<Category>()
			if (category != null) {
				disabledCategories.add(category)
				category.childCategories.forEach { category ->
					disabledCategories.add(category)
					disabledCategories.addAll(category.addChildCategory())
				}
			}
			if (parentCategory != null) disabledCategories.add(parentCategory)
			println(disabledCategories)
			val isDeleteEnabled = id != 0L
			state = state.copy(
				allCategories = categories,
				parentCategory = parentCategory,
				disabledCategories = disabledCategories.toList(),
				isDeleteEnabled = isDeleteEnabled,
				color = if (category == null) Color.Transparent else Color(category.color)
			)
		}
	}

	fun onAction(action: EditCategoryAction) {
		when (action) {
			is EditCategoryAction.ChangeParentCategory -> {
				state = state.copy(parentCategory = action.category)
			}

			EditCategoryAction.OnSaveClick -> {
				viewModelScope.launch {
					repository.saveCategory(Category(
						id = id,
						name = state.nameState.text.toString(),
						parentCategory = state.parentCategory,
						color = state.color.toArgb()
					))
					channel.send(EditCategoryEvent.FinishedSaving)
				}
			}

			is EditCategoryAction.ChangeOnDeleteCategory -> {
				state = state.copy(onDeleteCategory = action.category)
			}

			EditCategoryAction.DeleteConfirmed -> {
				val oldCategoryId = id
				val newCategoryId = state.onDeleteCategory?.id
				viewModelScope.launch {
					repository.updateCategoryIdsForTransactions(oldCategoryId, newCategoryId)
					repository.updateParentCategoryIds(oldCategoryId, state.parentCategory?.id)
					repository.deleteCategory(oldCategoryId)
					channel.send(EditCategoryEvent.FinishedSaving)
				}
			}

			is EditCategoryAction.ChangeColor -> {
				state = state.copy(color = action.color)
			}
		}
	}

	private fun Category.addChildCategory(): List<Category> {
		val categories = mutableListOf<Category>()
		childCategories.forEach { category ->
			categories.add(category)
			categories.addAll(category.addChildCategory())
		}
		return categories
	}
}