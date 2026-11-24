package vadimerenkov.krona.core.presentation.transaction

import vadimerenkov.krona.core.domain.Transaction

data class TransactionState(
	val transaction: Transaction? = null,
	val statsFor6Months: List<Double> = listOf(0.0)
)
