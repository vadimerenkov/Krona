package vadimerenkov.krona.core.presentation.debts

import vadimerenkov.krona.core.domain.Person

sealed interface DebtsAction {
	data class SelectPerson(val person: Person): DebtsAction
	data object SaveChangeClick: DebtsAction
}