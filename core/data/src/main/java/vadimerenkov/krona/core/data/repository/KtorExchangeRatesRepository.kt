package vadimerenkov.krona.core.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import vadimerenkov.krona.core.data.BuildConfig
import vadimerenkov.krona.core.data.dao.ExchangeRatesDao
import vadimerenkov.krona.core.data.dto.ExchangeRatesDto
import vadimerenkov.krona.core.data.entities.ExchangeRateData
import vadimerenkov.krona.core.data.networking.safeCall
import vadimerenkov.krona.core.domain.ExchangeRatesRepository
import vadimerenkov.krona.core.domain.util.map
import java.util.Currency

class KtorExchangeRatesRepository(
	private val httpClient: HttpClient,
	private val exchangeRatesDao: ExchangeRatesDao
): ExchangeRatesRepository {

	override suspend fun fetchRates() {
		val apiKey = BuildConfig.EXCHANGE_RATES_API_KEY
		val t = safeCall<ExchangeRatesDto> {
			httpClient.get(
				urlString = "https://v6.exchangerate-api.com/v6/$apiKey/latest/USD"
			) {

			}
		}
		t.map { dto ->
			val rates = dto.rates.map { (currency, rate) ->
				ExchangeRateData(currency, rate)
			}
			exchangeRatesDao.saveRates(rates)
		}
	}

	override suspend fun getAmountInCurrency(
		amount: Double, // 100
		currency1: Currency, // USD
		currency2: Currency // AMD
	): Double? {
		if (currency1 == currency2) return amount

		val usdRate1 = exchangeRatesDao.getUsdRateForCurrency(currency1.currencyCode) // 1.0
		val usdRate2 = exchangeRatesDao.getUsdRateForCurrency(currency2.currencyCode) // 360.0
		if (usdRate1 == null || usdRate2 == null) return null
		val amountInUsd = amount / usdRate1
		val amountInCurrency2 = amountInUsd * usdRate2
		return amountInCurrency2
	}
}