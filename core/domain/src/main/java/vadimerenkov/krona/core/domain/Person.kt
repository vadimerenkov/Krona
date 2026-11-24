package vadimerenkov.krona.core.domain

import java.time.ZonedDateTime

data class Person(
	val name: String,
	val amountOwed: Double,
	val lastUpdated: ZonedDateTime,
	val isMe: Boolean = false
)
