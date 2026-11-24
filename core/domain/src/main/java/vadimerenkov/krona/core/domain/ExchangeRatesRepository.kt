package vadimerenkov.krona.core.domain

import java.util.Currency

interface ExchangeRatesRepository {

	suspend fun fetchRates()

	suspend fun getAmountInCurrency(
		amount: Double,
		currency1: Currency,
		currency2: Currency
	): Double?
}