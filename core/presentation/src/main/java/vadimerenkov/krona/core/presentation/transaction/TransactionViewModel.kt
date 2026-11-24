package vadimerenkov.krona.core.presentation.transaction

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vadimerenkov.krona.core.domain.StatisticsRepository
import vadimerenkov.krona.core.domain.TransactionsRepository

class TransactionViewModel(
	private val id: Long,
	private val transactionsRepository: TransactionsRepository,
	private val statisticsRepository: StatisticsRepository
): ViewModel() {

	var state by mutableStateOf(TransactionState())
	private val channel = Channel<TransactionEvent>()
	val events = channel.receiveAsFlow()

	init {
		viewModelScope.launch {
			val transaction = transactionsRepository.getTransactionById(id)
			val stats = statisticsRepository.get6MonthsStatsForTransaction(transaction)
			println(stats)
			state = state.copy(transaction = transaction, statsFor6Months = stats)
		}
	}

	fun onAction(action: TransactionAction) {
		when (action) {
			TransactionAction.DeleteConfirmed -> {
				viewModelScope.launch {
					transactionsRepository.deleteTransaction(id)
					channel.send(TransactionEvent.SavingFinished)
				}
			}
			else -> Unit
		}
	}
}