package vadimerenkov.krona.core.domain

import java.time.ZonedDateTime

data class Transaction(
	val id: Long = 0,
	val account: Account,
	val amount: Double,
	val timestamp: ZonedDateTime,
	val category: Category? = null,
	val place: String? = null,
	val note: String? = null,
	val type: TransactionType = TransactionType.INCOME_EXPENSE,
	val payees: List<Person> = emptyList(),
	val splitAmounts: List<Double> = emptyList()
)
