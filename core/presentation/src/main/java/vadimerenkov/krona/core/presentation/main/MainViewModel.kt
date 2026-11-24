package vadimerenkov.krona.core.presentation.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vadimerenkov.krona.core.domain.ExchangeRatesRepository
import vadimerenkov.krona.core.domain.TransactionsRepository
import vadimerenkov.krona.core.domain.settings.LAST_UPDATED_RATES
import vadimerenkov.krona.core.domain.settings.Settings
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

class MainViewModel(
	private val repository: TransactionsRepository,
	private val exchangeRatesRepository: ExchangeRatesRepository,
	private val settings: Settings
): ViewModel() {

	var state by mutableStateOf(MainState())

	private val channel = Channel<MainEvent>()
	val events = channel.receiveAsFlow()

	init {
		viewModelScope.launch {
			val lastUpdatedRates = settings.getSetting(LAST_UPDATED_RATES)
			if (lastUpdatedRates == null || Instant.ofEpochSecond(lastUpdatedRates).atZone(ZoneId.systemDefault()).toLocalDate() != LocalDate.now()) {
				exchangeRatesRepository.fetchRates()
				settings.saveSetting(LAST_UPDATED_RATES, ZonedDateTime.now().toEpochSecond())
			}
			val accounts = repository.getAccounts().first()
			if (accounts.isEmpty()) {
				channel.send(MainEvent.StartOnboarding)
			}
		}
	}

	fun onAction(action: MainAction) {
		when (action) {
			is MainAction.ChangePage -> {
				state = state.copy(
					currentPage = action.page
				)
			}
			else -> Unit
		}
	}
}