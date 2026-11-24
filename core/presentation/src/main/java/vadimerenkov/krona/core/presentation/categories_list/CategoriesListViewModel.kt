package vadimerenkov.krona.core.presentation.categories_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import vadimerenkov.krona.core.domain.TransactionsRepository

class CategoriesListViewModel(
	private val repository: TransactionsRepository
): ViewModel() {

	var state by mutableStateOf(CategoriesListState())

	init {
		repository.getCategories()
			.onEach { categories ->
				state = state.copy(categories = categories)
			}.launchIn(viewModelScope)
	}

	fun onAction(action: CategoriesListAction) {
		
	}
}