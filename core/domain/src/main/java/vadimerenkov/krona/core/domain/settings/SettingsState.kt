package vadimerenkov.krona.core.domain.settings

import java.util.Currency

data class SettingsState(
	val defaultCurrency: Currency = Currency.getInstance("USD"),
	val language: Language? = null,
	val theme: Theme = Theme.DEVICE
)
