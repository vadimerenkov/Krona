package vadimerenkov.krona

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.koinInject
import vadimerenkov.krona.core.domain.settings.LANGUAGE
import vadimerenkov.krona.core.domain.settings.Language
import vadimerenkov.krona.core.domain.settings.Settings
import vadimerenkov.krona.core.domain.settings.Theme
import vadimerenkov.krona.core.presentation.navigation.MainNavigationRoot
import vadimerenkov.krona.core.presentation.theme.KronaTheme
import java.util.Locale

class MainActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			val settings = koinInject<Settings>()
			val settingsState by settings.state.collectAsState()

			LaunchedEffect(true) {
				if (settingsState.language == null) {
					val defaultLocale = Locale.getDefault()
					val matchedLanguage = Language.entries.find { language ->
						language.code == defaultLocale.toLanguageTag()
					}
					if (matchedLanguage == null) {
						settings.saveSetting(LANGUAGE, Language.ENGLISH.name)
					} else {
						settings.saveSetting(LANGUAGE, matchedLanguage.name)
					}
				}
			}

			val theme = settingsState.theme
			val isDarkTheme = if (theme == Theme.DEVICE) isSystemInDarkTheme() else theme == Theme.DARK

			KronaTheme(
				isDarkTheme = isDarkTheme
			) {
				MainNavigationRoot()
			}
		}
	}
}