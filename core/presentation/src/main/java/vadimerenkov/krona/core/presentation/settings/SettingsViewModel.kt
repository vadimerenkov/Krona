package vadimerenkov.krona.core.presentation.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vadimerenkov.krona.core.domain.settings.CURRENCY
import vadimerenkov.krona.core.domain.settings.LANGUAGE
import vadimerenkov.krona.core.domain.settings.Settings
import vadimerenkov.krona.core.domain.settings.THEME

class SettingsViewModel(
	val settings: Settings
): ViewModel() {

	fun onAction(action: SettingsAction) {
		when (action) {
			is SettingsAction.ChangeLanguage -> {
				viewModelScope.launch {
					settings.saveSetting(LANGUAGE, action.language.name)
					AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(action.language.code))
				}
			}

			is SettingsAction.ChangeTheme -> {
				viewModelScope.launch {
					settings.saveSetting(THEME, action.theme.name)
				}
			}

			is SettingsAction.ChangeDefaultCurrency -> {
				viewModelScope.launch {
					settings.saveSetting(CURRENCY, action.currency.currencyCode)
				}
			}
		}
	}
}