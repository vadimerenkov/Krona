package vadimerenkov.krona.core.data.di

import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import vadimerenkov.krona.core.data.csv.cashew.CashewCsvImporter
import vadimerenkov.krona.core.data.csv.zen.ZenCsvImporter
import vadimerenkov.krona.core.data.database.KronaDatabase
import vadimerenkov.krona.core.data.networking.HttpClientFactory
import vadimerenkov.krona.core.data.repository.KtorExchangeRatesRepository
import vadimerenkov.krona.core.data.repository.RoomLocalStatisticsRepository
import vadimerenkov.krona.core.data.repository.RoomLocalTransactionsRepository
import vadimerenkov.krona.core.domain.ExchangeRatesRepository
import vadimerenkov.krona.core.domain.StatisticsRepository
import vadimerenkov.krona.core.domain.TransactionsRepository
import vadimerenkov.krona.core.domain.csv.CashewImporter
import vadimerenkov.krona.core.domain.csv.CsvExporter
import vadimerenkov.krona.core.domain.csv.ZenImporter
import vadimerenkov.krona.core.data.csv.CsvExporter as csvExporter

val coreDataModule = module {
	singleOf(::RoomLocalTransactionsRepository) bind TransactionsRepository::class
	singleOf(::RoomLocalStatisticsRepository) bind StatisticsRepository::class
	singleOf(::KtorExchangeRatesRepository) bind ExchangeRatesRepository::class
	singleOf(::ZenCsvImporter) bind ZenImporter::class
	singleOf(::CashewCsvImporter) bind CashewImporter::class
	singleOf(::csvExporter) bind CsvExporter::class

	single {
		CoroutineScope(Dispatchers.Default + SupervisorJob())
	}

	single {
		HttpClientFactory.create()
	}

	single {
		Room.databaseBuilder(
			androidApplication(),
			KronaDatabase::class.java,
			"krona.db"
		).build()
	}

	single {
		get<KronaDatabase>().accountsDao
	}

	single {
		get<KronaDatabase>().transactionsDao
	}

	single {
		get<KronaDatabase>().categoriesDao
	}

	single {
		get<KronaDatabase>().personDao
	}

	single {
		get<KronaDatabase>().exchangeRatesDao
	}

	single {
		get<KronaDatabase>().placesDao
	}
}