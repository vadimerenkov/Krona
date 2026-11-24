package vadimerenkov.krona.core.domain.settings

import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

val CURRENCY = stringPreferencesKey("CURRENCY")
val LAST_UPDATED_RATES = longPreferencesKey("LAST_UPDATED_RATES")
val LANGUAGE = stringPreferencesKey("LANGUAGE")
val THEME = stringPreferencesKey("THEME")