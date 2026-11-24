package vadimerenkov.krona.core.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import vadimerenkov.krona.core.data.entities.AccountData
import vadimerenkov.krona.core.data.entities.AccountWithAmount
import vadimerenkov.krona.core.data.entities.TransactionData
import vadimerenkov.krona.core.data.mappers.toAccountWithAmount

@Dao
interface AccountsDao {

	@Upsert
	suspend fun saveAccount(account: AccountData)

	@Upsert
	suspend fun saveAccounts(accounts: List<AccountData>)

	@Query("SELECT * FROM accountdata")
	fun getAccounts(): Flow<List<AccountData>>

	@Query("SELECT SUM(amount) FROM transactiondata WHERE accountId = :id")
	suspend fun getAmountForAccount(id: Long): Double

	@Query("SELECT * FROM transactiondata")
	fun getAllTransactions(): Flow<List<TransactionData>>

	fun getAccountsWithAmounts(): Flow<List<AccountWithAmount>> {
		return getAccounts()
			.combine(getAllTransactions()) { accounts, transactions ->
				accounts
			}
			.map { accounts ->
				accounts.map { account ->
					val amount = getAmountForAccount(account.id) + account.initialAmount
					account.toAccountWithAmount(amount)
				}
			}
	}

	@Query("SELECT * FROM accountdata WHERE isDefault = 1")
	suspend fun getDefaultAccount(): AccountData

	@Query("SELECT * FROM accountdata WHERE id = :id")
	suspend fun getAccountById(id: Long): AccountData

	@Query("SELECT * FROM accountdata WHERE name = :name")
	suspend fun getAccountByName(name: String): AccountData

	@Query("DELETE FROM accountdata WHERE id = :accountId")
	suspend fun deleteAccount(accountId: Long)

	@Query("SELECT currencyCode FROM accountdata WHERE id = :id")
	suspend fun getCurrencyForAccountId(id: Long): String
}