package vadimerenkov.krona.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import vadimerenkov.krona.core.data.dao.AccountsDao
import vadimerenkov.krona.core.data.dao.CategoriesDao
import vadimerenkov.krona.core.data.dao.ExchangeRatesDao
import vadimerenkov.krona.core.data.dao.PersonDao
import vadimerenkov.krona.core.data.dao.PlacesDao
import vadimerenkov.krona.core.data.dao.TransactionsDao
import vadimerenkov.krona.core.data.entities.AccountData
import vadimerenkov.krona.core.data.entities.CategoryData
import vadimerenkov.krona.core.data.entities.ExchangeRateData
import vadimerenkov.krona.core.data.entities.PersonData
import vadimerenkov.krona.core.data.entities.PlaceData
import vadimerenkov.krona.core.data.entities.TransactionData

@Database(
	entities = [
		AccountData::class,
		TransactionData::class,
		CategoryData::class,
		PersonData::class,
		ExchangeRateData::class,
		PlaceData::class
	],
	version = 1
)
abstract class KronaDatabase: RoomDatabase() {

	abstract val accountsDao: AccountsDao
	abstract val transactionsDao: TransactionsDao
	abstract val categoriesDao: CategoriesDao
	abstract val personDao: PersonDao
	abstract val exchangeRatesDao: ExchangeRatesDao
	abstract val placesDao: PlacesDao
}