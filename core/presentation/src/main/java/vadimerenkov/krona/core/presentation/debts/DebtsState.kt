package vadimerenkov.krona.core.presentation.debts

import vadimerenkov.krona.core.domain.Person

data class DebtsState(
	val persons: List<Person> = emptyList()
)
