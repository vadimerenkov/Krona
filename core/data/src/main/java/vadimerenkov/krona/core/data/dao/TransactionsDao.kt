package vadimerenkov.krona.core.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import vadimerenkov.krona.core.data.entities.TransactionData

@Dao
interface TransactionsDao {

	@Upsert
	suspend fun saveTransaction(transaction: TransactionData)

	@Upsert
	suspend fun saveTransactions(transactions: List<TransactionData>)

	@Query("DELETE FROM transactiondata WHERE id = :id")
	suspend fun deleteTransaction(id: Long)

	@Query("SELECT * FROM transactiondata ORDER BY timestamp DESC")
	fun getAllTransactions(): Flow<List<TransactionData>>

	@Query("SELECT * FROM transactiondata WHERE accountId = :accountId ORDER BY timestamp DESC")
	suspend fun getTransactionsForAccount(accountId: Long): List<TransactionData>

	@Query("SELECT * FROM transactiondata ORDER BY timestamp DESC")
	fun pagingSource(): PagingSource<Int, TransactionData>

	@Query("SELECT * FROM transactiondata WHERE id = :id")
	suspend fun getTransactionById(id: Long): TransactionData

	@Query("SELECT * FROM transactiondata WHERE timestamp > :startDate AND timestamp < :endDate AND categoryId = :categoryId AND type = 'INCOME_EXPENSE' ORDER BY timestamp DESC")
	suspend fun getTransactionsForDateForCategory(startDate: Long, endDate: Long, categoryId: Long): List<TransactionData>

	@Query("SELECT * FROM transactiondata WHERE timestamp > :startDate AND timestamp < :endDate AND categoryId IS NULL AND type = 'INCOME_EXPENSE' ORDER BY timestamp DESC")
	suspend fun getTransactionsForDateWithoutCategory(startDate: Long, endDate: Long): List<TransactionData>

	@Query("UPDATE transactiondata SET categoryId = :newCategoryId WHERE categoryId = :oldCategoryId")
	suspend fun updateCategoryIds(oldCategoryId: Long, newCategoryId: Long?)

	@Query("SELECT COUNT(*) FROM transactiondata WHERE categoryId = :categoryId")
	suspend fun countTransactionsForCategory(categoryId: Long): Long

	@Query("SELECT * FROM transactiondata WHERE timestamp > :startDate AND timestamp < :endDate")
	fun getAllTransactionsForDate(startDate: Long, endDate: Long): Flow<List<TransactionData>>
}