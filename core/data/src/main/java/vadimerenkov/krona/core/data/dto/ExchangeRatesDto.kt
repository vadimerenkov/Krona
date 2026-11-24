package vadimerenkov.krona.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRatesDto(
	@SerialName("conversion_rates") val rates: Map<String, Double>
)
