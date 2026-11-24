package vadimerenkov.krona.core.presentation.transactions_list

import vadimerenkov.krona.core.domain.Transaction

data class TransactionsListState(
	val transactions: List<Transaction> = emptyList()
)
