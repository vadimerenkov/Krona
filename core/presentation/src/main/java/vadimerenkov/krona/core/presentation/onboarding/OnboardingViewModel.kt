package vadimerenkov.krona.core.presentation.onboarding

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vadimerenkov.krona.core.domain.Account
import vadimerenkov.krona.core.domain.Category
import vadimerenkov.krona.core.domain.Person
import vadimerenkov.krona.core.domain.TransactionsRepository
import vadimerenkov.krona.core.domain.settings.CURRENCY
import vadimerenkov.krona.core.domain.settings.Settings
import vadimerenkov.krona.core.presentation.R
import vadimerenkov.krona.core.presentation.util.palette
import java.time.ZonedDateTime

class OnboardingViewModel(
	private val repository: TransactionsRepository,
	private val settings: Settings
): ViewModel() {

	var state by mutableStateOf(OnboardingState())

	private val channel = Channel<OnboardingEvent>()
	val events = channel.receiveAsFlow()

	fun onAction(action: OnboardingAction) {
		when (action) {
			is OnboardingAction.ChangeCurrency -> {
				state = state.copy(currency = action.currency)
			}

			is OnboardingAction.OnSaveClick -> {
				val me = action.context.getString(R.string.me)
				val categories = getCategories(action.context)
				viewModelScope.launch {
					repository.saveAccount(Account(
						id = 0,
						currency = state.currency,
						name = state.accountNameState.text.toString(),
						isDefault = true
					))
					repository.savePerson(Person(
						name = me,
						amountOwed = 0.0,
						lastUpdated = ZonedDateTime.now(),
						isMe = true
					))
					repository.saveCategories(categories)
					settings.saveSetting(CURRENCY, state.currency.currencyCode)
					channel.send(OnboardingEvent.SavingFinished)
				}
			}
		}
	}

	private fun getCategories(context: Context): List<Category> {
		val food = Category(
			id = 1,
			name = context.getString(R.string.food),
			color = palette[0].toArgb()
		)
		val groceries = Category(
			id = 2,
			name = context.getString(R.string.groceries),
			parentCategory = food,
			color = palette[1].toArgb()
		)
		val restaurants = Category(
			id = 3,
			name = context.getString(R.string.restaurants),
			parentCategory = food,
			color = palette[2].toArgb()
		)
		val living = Category(
			id = 4,
			name = context.getString(R.string.living),
			color = palette[3].toArgb()
		)
		val rent = Category(
			id = 5,
			name = context.getString(R.string.rent),
			parentCategory = living,
			color = palette[4].toArgb()
		)
		val hotel = Category(
			id = 6,
			name = context.getString(R.string.hotel),
			parentCategory = living,
			color = palette[5].toArgb()
		)
		val utilities = Category(
			id = 7,
			name = context.getString(R.string.utilities),
			parentCategory = living,
			color = palette[6].toArgb()
		)
		val electricity = Category(
			id = 8,
			name = context.getString(R.string.electricity),
			parentCategory = utilities,
			color = palette[7].toArgb()
		)
		val natural_gas = Category(
			id = 9,
			name = context.getString(R.string.natural_gas),
			parentCategory = utilities,
			color = palette[8].toArgb()
		)
		val hot_water = Category(
			id = 10,
			name = context.getString(R.string.hot_water),
			parentCategory = utilities,
			color = palette[9].toArgb()
		)
		val cold_water = Category(
			id = 11,
			name = context.getString(R.string.cold_water),
			parentCategory = utilities,
			color = palette[10].toArgb()
		)
		val heating = Category(
			id = 12,
			name = context.getString(R.string.heating),
			parentCategory = utilities,
			color = palette[11].toArgb()
		)
		val taxes = Category(
			id = 13,
			name = context.getString(R.string.taxes),
			color = palette[12].toArgb()
		)
		val entertainment = Category(
			id = 14,
			name = context.getString(R.string.entertainment),
			color = palette[13].toArgb()
		)
		val cinema = Category(
			id = 15,
			name = context.getString(R.string.cinema),
			parentCategory = entertainment,
			color = palette[14].toArgb()
		)
		val live_events = Category(
			id = 16,
			name = context.getString(R.string.live_events),
			parentCategory = entertainment,
			color = palette[15].toArgb()
		)
		val transport = Category(
			id = 17,
			name = context.getString(R.string.transport),
			color = palette[1].toArgb()
		)
		val car = Category(
			id = 18,
			name = context.getString(R.string.car),
			parentCategory = transport,
			color = palette[2].toArgb()
		)
		val car_gas = Category(
			id = 19,
			name = context.getString(R.string.car_gas),
			parentCategory = car,
			color = palette[3].toArgb()
		)
		val public_transport = Category(
			id = 20,
			name = context.getString(R.string.public_transport),
			parentCategory = transport,
			color = palette[4].toArgb()
		)
		val taxi = Category(
			id = 21,
			name = context.getString(R.string.taxi),
			parentCategory = transport,
			color = palette[5].toArgb()
		)
		val services = Category(
			id = 22,
			name = context.getString(R.string.services),
			color = palette[6].toArgb()
		)
		val subscriptions = Category(
			id = 23,
			name = context.getString(R.string.subscriptions),
			color = palette[7].toArgb()
		)
		val phone = Category(
			id = 24,
			name = context.getString(R.string.phone),
			parentCategory = subscriptions,
			color = palette[8].toArgb()
		)
		val internet = Category(
			id = 25,
			name = context.getString(R.string.internet),
			parentCategory = subscriptions,
			color = palette[9].toArgb()
		)
		val health = Category(
			id = 26,
			name = context.getString(R.string.health),
			color = palette[10].toArgb()
		)
		val doctor = Category(
			id = 27,
			name = context.getString(R.string.doctor),
			parentCategory = health,
			color = palette[11].toArgb()
		)
		val medicine = Category(
			id = 28,
			name = context.getString(R.string.medicine),
			parentCategory = health,
			color = palette[12].toArgb()
		)
		val fees = Category(
			id = 29,
			name = context.getString(R.string.fees),
			color = palette[13].toArgb()
		)
		val purchases = Category(
			id = 30,
			name = context.getString(R.string.purchases),
			color = palette[14].toArgb()
		)
		val clothing = Category(
			id = 31,
			name = context.getString(R.string.clothing),
			parentCategory = purchases,
			color = palette[15].toArgb()
		)
		val gadgets = Category(
			id = 32,
			name = context.getString(R.string.gadgets),
			parentCategory = purchases,
			color = palette[0].toArgb()
		)
		val home_goods = Category(
			id = 33,
			name = context.getString(R.string.home_goods),
			parentCategory = purchases,
			color = palette[1].toArgb()
		)
		val income = Category(
			id = 34,
			name = context.getString(R.string.income),
			color = palette[2].toArgb()
		)
		val salary = Category(
			id = 35,
			name = context.getString(R.string.salary),
			parentCategory = income,
			color = palette[3].toArgb()
		)
		val cashback = Category(
			id = 36,
			name = context.getString(R.string.cashback),
			parentCategory = income,
			color = palette[4].toArgb()
		)
		val refunds = Category(
			id = 37,
			name = context.getString(R.string.refunds),
			parentCategory = income,
			color = palette[5].toArgb()
		)

		return listOf(food, groceries, restaurants, living, rent, hotel, utilities, electricity, natural_gas, hot_water, cold_water, heating,
			taxes, entertainment, cinema, live_events, transport, car, car_gas, public_transport, taxi, services, subscriptions, phone, internet, health,
			doctor, medicine, fees, purchases, clothing, gadgets, home_goods, income, salary, cashback, refunds)
	}
}