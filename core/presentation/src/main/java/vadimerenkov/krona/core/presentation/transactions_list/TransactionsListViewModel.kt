package vadimerenkov.krona.core.presentation.transactions_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import vadimerenkov.krona.core.domain.TransactionsRepository

class TransactionsListViewModel(
	private val repository: TransactionsRepository
): ViewModel() {
	var state by mutableStateOf(TransactionsListState())

	val transactions = repository.pagedTransactions().cachedIn(viewModelScope)

	fun onAction(action: TransactionsListAction) {

	}
}