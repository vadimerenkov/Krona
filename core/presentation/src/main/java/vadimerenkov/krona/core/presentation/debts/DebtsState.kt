package vadimerenkov.krona.core.presentation.debts

import androidx.compose.foundation.text.input.TextFieldState
import vadimerenkov.krona.core.domain.Person

data class DebtsState(
	val persons: List<Person> = emptyList(),
	val selectedPerson: Person? = null,
	val selectedDebtState: TextFieldState = TextFieldState()
)
