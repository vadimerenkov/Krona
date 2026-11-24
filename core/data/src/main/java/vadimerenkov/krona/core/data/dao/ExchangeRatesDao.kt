package vadimerenkov.krona.core.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import vadimerenkov.krona.core.data.entities.ExchangeRateData

@Dao
interface ExchangeRatesDao {

	@Upsert
	suspend fun saveRates(rates: List<ExchangeRateData>)

	@Query("SELECT rateToUsd FROM exchangeratedata WHERE currency = :currency")
	suspend fun getUsdRateForCurrency(currency: String): Double?
}