package vadimerenkov.krona.core.domain

import java.time.LocalDate

sealed class TransactionListItem {

	class TransactionItem(val transaction: Transaction): TransactionListItem()
	class DateHeader(val date: LocalDate?): TransactionListItem()
}