package vadimerenkov.krona.core.presentation.edit_transaction

import vadimerenkov.krona.core.domain.Account
import vadimerenkov.krona.core.domain.Category
import vadimerenkov.krona.core.domain.TransactionType
import java.time.LocalDate
import java.time.LocalTime

sealed interface EditTransactionAction {
	data object OnSaveClick: EditTransactionAction
	data class ChangeAccount(val account: Account): EditTransactionAction
	data class ChangeCategory(val category: Category?): EditTransactionAction
	data class ChangeDate(val date: LocalDate): EditTransactionAction
	data class ChangeTime(val time: LocalTime): EditTransactionAction
	data class ChangeOutgoingAccount(val account: Account): EditTransactionAction
	data class ChangeIncomingAccount(val account: Account): EditTransactionAction
	data class ChangeType(val type: TransactionType): EditTransactionAction
	data object AddPersonAmount: EditTransactionAction
	data class RemovePersonAmount(val personAmount: PersonAmount): EditTransactionAction
}