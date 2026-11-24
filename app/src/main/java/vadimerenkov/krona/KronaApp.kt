package vadimerenkov.krona

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import vadimerenkov.krona.core.data.di.coreDataModule
import vadimerenkov.krona.core.presentation.di.corePresentationModule
import vadimerenkov.krona.di.appModule

val Context.dataStore by preferencesDataStore("krona_settings")
class KronaApp: Application() {

	override fun onCreate() {
		super.onCreate()

		startKoin {
			androidContext(this@KronaApp)
			modules(
				appModule,
				corePresentationModule,
				coreDataModule
			)
		}
	}
}