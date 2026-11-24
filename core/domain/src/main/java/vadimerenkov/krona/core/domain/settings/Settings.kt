package vadimerenkov.krona.core.domain.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.util.Currency

class Settings(
	private val dataStore: DataStore<Preferences>,
	private val applicationScope: CoroutineScope
) {

	private val _state = MutableStateFlow(SettingsState())
	val state = _state.asStateFlow()

	init {
		dataStore.data
			.map { prefs ->
				SettingsState(
					defaultCurrency = Currency.getInstance(prefs[CURRENCY] ?: "USD"),
					language = if (prefs[LANGUAGE] == null) null else Language.valueOf(prefs[LANGUAGE]!!),
					theme = Theme.valueOf(prefs[THEME] ?: Theme.DEVICE.name)
				)
			}
			.onEach { newState ->
				_state.update { newState }
			}.launchIn(applicationScope)
	}

	suspend fun <T> saveSetting(key: Preferences.Key<T>, value: T) {
		dataStore.edit { preferences ->
			preferences[key] = value
		}
	}

	suspend fun <T> getSetting(key: Preferences.Key<T>): T? {
		return dataStore.data.first()[key]
	}
}