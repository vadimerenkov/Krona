package vadimerenkov.krona.core.presentation.accounts_list

import vadimerenkov.krona.core.domain.Account
import java.util.Currency

data class AccountsListState(
	val accounts: List<Account> = emptyList(),
	val totalBalance: Double = 0.0,
	val defaultCurrency: Currency = Currency.getInstance("USD"),
	val showArchivedAccounts: Boolean = false
)
