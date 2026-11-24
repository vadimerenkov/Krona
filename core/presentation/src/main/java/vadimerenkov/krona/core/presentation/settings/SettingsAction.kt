package vadimerenkov.krona.core.presentation.settings

import vadimerenkov.krona.core.domain.settings.Language
import vadimerenkov.krona.core.domain.settings.Theme
import java.util.Currency

sealed interface SettingsAction {
	data class ChangeLanguage(val language: Language): SettingsAction
	data class ChangeTheme(val theme: Theme): SettingsAction
	data class ChangeDefaultCurrency(val currency: Currency): SettingsAction
}