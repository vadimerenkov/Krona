package vadimerenkov.krona.core.presentation.statistics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import vadimerenkov.krona.core.domain.Category
import vadimerenkov.krona.core.domain.ExchangeRatesRepository
import vadimerenkov.krona.core.domain.StatisticsRepository
import vadimerenkov.krona.core.domain.Transaction
import vadimerenkov.krona.core.domain.settings.Settings
import java.time.YearMonth
import java.time.ZoneId
import kotlin.math.absoluteValue

class StatisticsViewModel(
	private val statisticsRepository: StatisticsRepository,
	private val exchangeRatesRepository: ExchangeRatesRepository,
	private val settings: Settings
): ViewModel() {

	var state by mutableStateOf(StatisticsState())

	val currentMonth = YearMonth.now()
	val start = currentMonth.atDay(1).atStartOfDay().atZone(ZoneId.systemDefault())
	val end = currentMonth.atEndOfMonth().atTime(23, 59).atZone(ZoneId.systemDefault())

	init {
		statisticsRepository.getMonthStatsForAllCategories()
			.onEach { _ ->
				val stats = statisticsRepository.getMonthStatsForCategory(start, end, null)
				state = state.copy(statsForMonth = stats.mapTransactionsToAmount())
				println(stats)
			}.launchIn(viewModelScope)
	}

	private suspend fun Map<Category?, List<Transaction>>.mapTransactionsToAmount(): Map<Category?, Double> {
		return mapValues { (category, list) ->
			list.sumOf { transaction ->
				val amount = if (transaction.splitAmounts.isNotEmpty()) {
					transaction.payees.zip(transaction.splitAmounts) { person, amount ->
						if (person.isMe) {
							amount
						} else {
							null
						}
					}.filterNotNull().first()
				} else {
					transaction.amount
				}
				val accountCurrency = transaction.account.currency
				val amountInDefaultCurrency = exchangeRatesRepository.getAmountInCurrency(amount, accountCurrency, settings.state.value.defaultCurrency)
				amountInDefaultCurrency ?: 0.0
			}.absoluteValue
		}
			.filterValues { it != 0.0 }
			.toList()
			.sortedByDescending { it.second }
			.toMap()
	}
}