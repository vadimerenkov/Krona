package vadimerenkov.krona.core.domain

import java.util.Currency

data class Account(
	val id: Long,
	val currency: Currency,
	val name: String,
	val initialAmount: Double = 0.0,
	val amount: Double = 0.0,
	val isDefault: Boolean = false,
	val isArchived: Boolean = false
)
