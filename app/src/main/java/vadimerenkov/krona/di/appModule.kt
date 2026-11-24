package vadimerenkov.krona.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import vadimerenkov.krona.core.domain.settings.Settings
import vadimerenkov.krona.dataStore

val appModule = module {
	single { androidContext().dataStore }
	singleOf(::Settings)
}