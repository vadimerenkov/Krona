package vadimerenkov.krona.core.presentation.settings.util

import vadimerenkov.krona.core.domain.settings.Theme
import vadimerenkov.krona.core.presentation.R

val Theme.stringRes: Int
	get() = when (this) {
		Theme.LIGHT -> R.string.light_theme
		Theme.DARK -> R.string.dark_theme
		Theme.DEVICE -> R.string.device_theme
	}