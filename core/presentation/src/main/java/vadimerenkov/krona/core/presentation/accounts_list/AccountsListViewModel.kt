package vadimerenkov.krona.core.presentation.accounts_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import vadimerenkov.krona.core.domain.ExchangeRatesRepository
import vadimerenkov.krona.core.domain.TransactionsRepository
import vadimerenkov.krona.core.domain.settings.Settings

class AccountsListViewModel(
	private val transactionsRepository: TransactionsRepository,
	private val exchangeRatesRepository: ExchangeRatesRepository,
	private val settings: Settings
): ViewModel() {

	var state by mutableStateOf(AccountsListState())

	init {
		transactionsRepository.getAccounts()
			.onEach { accounts ->
				val defaultCurrency = settings.state.value.defaultCurrency
				val totalBalance = accounts
					.filter { !it.isArchived }
					.mapNotNull { account ->
						exchangeRatesRepository.getAmountInCurrency(account.amount, account.currency, defaultCurrency)
					}.sum()

				state = state.copy(accounts = accounts, totalBalance = totalBalance, defaultCurrency = defaultCurrency)
			}
			.launchIn(viewModelScope)
	}

	fun onAction(action: AccountsListAction) {
		when (action) {
			AccountsListAction.OnShowArchivedClick -> {
				state = state.copy(showArchivedAccounts = !state.showArchivedAccounts)
			}
			else -> Unit
		}
	}
}