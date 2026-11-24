package vadimerenkov.krona.core.presentation.edit_transaction

import androidx.compose.foundation.text.input.TextFieldState
import vadimerenkov.krona.core.domain.Account
import vadimerenkov.krona.core.domain.Category
import vadimerenkov.krona.core.domain.Person
import vadimerenkov.krona.core.domain.Place
import vadimerenkov.krona.core.domain.TransactionType
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

data class EditTransactionState(
	val amountState: TextFieldState = TextFieldState(),
	val category: Category? = null,
	val account: Account? = null,
	val placeState: TextFieldState = TextFieldState(),
	val noteState: TextFieldState = TextFieldState(),
	val date: LocalDate = LocalDate.now(),
	val time: LocalTime = LocalTime.now().truncatedTo(ChronoUnit.MINUTES),
	val allAccounts: List<Account> = emptyList(),
	val allCategories: List<Category> = emptyList(),
	val type: TransactionType = TransactionType.INCOME_EXPENSE,
	val outgoingAccount: Account? = null,
	val incomingAccount: Account? = null,
	val outgoingAmount: TextFieldState = TextFieldState(),
	val incomingAmount: TextFieldState = TextFieldState(),
	val personAmounts: List<PersonAmount> = emptyList(),
	val allPersons: List<Person> = emptyList(),
	val allPlaces: List<Place> = emptyList()
) {
	val isValid
		get() = when (type) {
			TransactionType.INCOME_EXPENSE -> {
				amountState.text.toString().toDoubleOrNull() != null && amountState.text.toString().toDoubleOrNull() != 0.0
						&&
						personAmounts.all { it.personState.text.toString().isNotBlank() }
						&&
						personAmounts.all { it.amountState.text.toString().toDoubleOrNull() != null }
			}
			TransactionType.TRANSFER -> {
				outgoingAccount != null && incomingAccount != null && outgoingAccount != incomingAccount
						&&
						outgoingAmount.text.toString().toDoubleOrNull() != null && outgoingAmount.text.toString().toDoubleOrNull() != 0.0
						&&
						incomingAmount.text.toString().toDoubleOrNull() != null && incomingAmount.text.toString().toDoubleOrNull() != 0.0
			}
		}

	val totalSplitAmount: Double
		get() {
			return personAmounts
				.mapNotNull { it.amountState.text.toString().toDoubleOrNull() }
				.sum()
		}

	val splitDifference: Double
		get() {
			return (amountState.text.toString().toDoubleOrNull() ?: 0.0) - totalSplitAmount
		}
}