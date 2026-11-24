package vadimerenkov.krona.core.presentation.debts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import vadimerenkov.krona.core.domain.TransactionsRepository

class DebtsViewModel(
	private val repository: TransactionsRepository
): ViewModel() {

	var state by mutableStateOf(DebtsState())

	init {
		repository.getPersons().onEach { persons ->
			state = state.copy(persons = persons)
		}.launchIn(viewModelScope)
	}
}