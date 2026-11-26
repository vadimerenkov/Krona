package vadimerenkov.krona.core.presentation.piechart

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vadimerenkov.krona.core.domain.Category
import vadimerenkov.krona.core.domain.ExchangeRatesRepository
import vadimerenkov.krona.core.domain.StatisticsRepository
import vadimerenkov.krona.core.domain.Transaction
import vadimerenkov.krona.core.domain.settings.Settings
import kotlin.math.absoluteValue

class PieChartViewModel(
	private val statisticsRepository: StatisticsRepository,
	private val exchangeRatesRepository: ExchangeRatesRepository,
	private val settings: Settings
): ViewModel() {

	var state by mutableStateOf(PieChartState())

	init {
		viewModelScope.launch {
			val stats = statisticsRepository
				.getMonthStatsForCategory(state.start, state.end, null)
			state = state.copy(statsForMonth = stats.mapTransactionsToAmount(), isLoading = false, transactions = stats.values.flatten())
		}
	}

	fun onAction(action: PieChartAction) {
		when (action) {
			is PieChartAction.OnCategoryClick -> {
				println(action.categoryId)
				viewModelScope.launch {
					val stats = when (state.type) {
						StatType.EXPENSE -> statisticsRepository.getMonthStatsForCategory(state.start, state.end, action.categoryId)
						StatType.INCOME -> statisticsRepository.getIncomeMonthStatsForCategory(state.start, state.end, action.categoryId)
					}
					val backStack = if (state.categoriesBackStack.lastOrNull() == action.categoryId) {
						state.categoriesBackStack
					} else {
						state.categoriesBackStack + action.categoryId
					}
					state = state.copy(statsForMonth = stats.mapTransactionsToAmount(), categoriesBackStack = backStack, transactions = stats.values.flatten())
				}
			}
			PieChartAction.OnBackClick -> {
				viewModelScope.launch {
					val backstack = state.categoriesBackStack - state.categoriesBackStack.lastOrNull()
					val stats = when (state.type) {
						StatType.EXPENSE -> statisticsRepository.getMonthStatsForCategory(state.start, state.end, backstack.lastOrNull())
						StatType.INCOME -> statisticsRepository.getIncomeMonthStatsForCategory(state.start, state.end, backstack.lastOrNull())
					}
					state = state.copy(statsForMonth = stats.mapTransactionsToAmount(), categoriesBackStack = backstack, transactions = stats.values.flatten())
				}
			}
			is PieChartAction.ChangeType -> {
				viewModelScope.launch {
					val stats = when (action.type) {
						StatType.EXPENSE -> statisticsRepository.getMonthStatsForCategory(state.start, state.end, null)
						StatType.INCOME -> statisticsRepository.getIncomeMonthStatsForCategory(state.start, state.end, null)
					}
					state = state.copy(statsForMonth = stats.mapTransactionsToAmount(), type = action.type, transactions = stats.values.flatten())
				}
			}

			is PieChartAction.SelectMonth -> {
				viewModelScope.launch {
					state = state.copy(selectedMonth = action.month)
					val stats = when (state.type) {
						StatType.EXPENSE -> statisticsRepository.getMonthStatsForCategory(state.start, state.end, null)
						StatType.INCOME -> statisticsRepository.getIncomeMonthStatsForCategory(state.start, state.end, null)
					}
					state = state.copy(statsForMonth = stats.mapTransactionsToAmount(), transactions = stats.values.flatten())
				}
			}
		}
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
					}.filterNotNull().firstOrNull()
				} else {
					transaction.amount
				}
				val accountCurrency = transaction.account.currency
				val amountInDefaultCurrency = exchangeRatesRepository.getAmountInCurrency(amount ?: 0.0, accountCurrency, settings.state.value.defaultCurrency)
				amountInDefaultCurrency ?: 0.0
			}.absoluteValue
		}
			.filterValues { it != 0.0 }
			.toList()
			.sortedByDescending { it.second }
			.toMap()
	}
}