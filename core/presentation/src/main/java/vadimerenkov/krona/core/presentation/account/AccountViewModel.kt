package vadimerenkov.krona.core.presentation.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vadimerenkov.krona.core.domain.ExchangeRatesRepository
import vadimerenkov.krona.core.domain.Transaction
import vadimerenkov.krona.core.domain.TransactionsRepository
import vadimerenkov.krona.core.domain.settings.Settings
import java.time.ZonedDateTime

class AccountViewModel(
	val id: Long,
	private val transactionsRepository: TransactionsRepository,
	private val exchangeRatesRepository: ExchangeRatesRepository,
	private val settings: Settings
): ViewModel() {

	var state by mutableStateOf(AccountState())

	private val channel = Channel<AccountEvent>()
	val events = channel.receiveAsFlow()

	init {
		viewModelScope.launch {
			val account = transactionsRepository.getAccountById(id)
			val transactions = transactionsRepository.getTransactionsForAccount(id)
			val defaultCurrency = settings.state.value.defaultCurrency
			val defaultAmount = if (defaultCurrency == account.currency) null else exchangeRatesRepository.getAmountInCurrency(account.amount, account.currency, defaultCurrency)
			state = state.copy(account = account, transactions = transactions, isLoading = false, defaultCurrency = defaultCurrency, amountInDefaultCurrency = defaultAmount)
		}
	}

	fun onAction(action: AccountAction) {
		when (action) {
			AccountAction.OnSaveChangeBalanceClick -> {
				viewModelScope.launch {
					transactionsRepository.saveTransaction(Transaction(
						account = state.account!!,
						amount = state.balanceDifference,
						timestamp = ZonedDateTime.now(),
					))
					val transactions = transactionsRepository.getTransactionsForAccount(id)
					val account = transactionsRepository.getAccountById(id)
					state = state.copy(transactions = transactions, account = account)
				}
			}
			AccountAction.OnDeleteClick -> {
				viewModelScope.launch {
					transactionsRepository.deleteAccount(id)
					channel.send(AccountEvent.FinishedSaving)
				}
			}

			AccountAction.OnArchiveClick -> {
				viewModelScope.launch {
					transactionsRepository.saveAccount(state.account!!.copy(isArchived = !state.account!!.isArchived))
				}
			}
			else -> Unit
		}
	}
}