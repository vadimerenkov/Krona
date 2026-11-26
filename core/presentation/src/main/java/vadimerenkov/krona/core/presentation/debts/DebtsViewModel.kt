package vadimerenkov.krona.core.presentation.debts

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import vadimerenkov.krona.core.domain.TransactionsRepository
import java.text.NumberFormat

class DebtsViewModel(
	private val repository: TransactionsRepository
): ViewModel() {

	var state by mutableStateOf(DebtsState())

	init {
		repository.getPersons().onEach { persons ->
			state = state.copy(persons = persons)
		}.launchIn(viewModelScope)
	}

	fun onAction(action: DebtsAction) {
		when (action) {
			is DebtsAction.SelectPerson -> {
				val formatter = NumberFormat.getInstance()
				formatter.maximumFractionDigits = 2
				state = state.copy(selectedPerson = action.person, selectedDebtState = TextFieldState(formatter.format(action.person.amountOwed)))
			}
			DebtsAction.SaveChangeClick -> {
				val amount = state.selectedDebtState.text.toString().replace(',', '.').toDouble()
				val person = state.selectedPerson!!.copy(amountOwed = amount)
				viewModelScope.launch {
					repository.savePerson(person)
				}
			}
		}
	}
}