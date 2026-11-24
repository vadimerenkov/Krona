package vadimerenkov.krona.core.presentation.account

import androidx.compose.foundation.text.input.TextFieldState
import vadimerenkov.krona.core.domain.Account
import vadimerenkov.krona.core.domain.Transaction
import java.util.Currency

data class AccountState(
	val isLoading: Boolean = true,
	val account: Account? = null,
	val transactions: List<Transaction> = emptyList(),
	val changeBalanceState: TextFieldState = TextFieldState(),
	val amountInDefaultCurrency: Double? = null,
	val defaultCurrency: Currency? = null
) {
	val balanceDifference: Double
		get() {
			val amount = account?.amount
			val newBalance = changeBalanceState.text.toString().toDoubleOrNull()
			return if (amount != null && newBalance != null) newBalance - amount else 0.0
		}
}