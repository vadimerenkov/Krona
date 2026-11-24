package vadimerenkov.krona.core.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ExchangeRateData(
	@PrimaryKey val currency: String,
	val rateToUsd: Double
)
