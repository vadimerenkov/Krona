package vadimerenkov.krona.core.domain

import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface StatisticsRepository {

	suspend fun get6MonthsStatsForTransaction(transaction: Transaction): List<Double>
	fun getMonthStatsForAllCategories(): Flow<Map<Category?, Double>>
	suspend fun getMonthStatsForCategory(monthStart: ZonedDateTime, monthEnd: ZonedDateTime, parentCategoryId: Long?): Map<Category?, List<Transaction>>
}