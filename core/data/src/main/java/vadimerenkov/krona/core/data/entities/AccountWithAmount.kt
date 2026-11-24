package vadimerenkov.krona.core.data.entities

import java.util.Currency

data class AccountWithAmount(
	val id: Long,
	val currency: Currency,
	val name: String,
	val initialAmount: Double,
	val amount: Double,
	val isDefault: Boolean,
	val isArchived: Boolean
)
