package vadimerenkov.krona.core.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface TransactionsRepository {

	suspend fun saveAccount(account: Account)
	suspend fun saveAccounts(accounts: List<Account>)
	suspend fun saveTransaction(transaction: Transaction)
	suspend fun saveTransactions(transactions: List<Transaction>)
	suspend fun deleteTransaction(id: Long)
	suspend fun savePerson(person: Person)
	suspend fun savePersons(persons: List<Person>)
	fun getPersons(): Flow<List<Person>>
	suspend fun getPersonByName(name: String): Person?
	fun getAccounts(): Flow<List<Account>>
	suspend fun getDefaultAccount(): Account
	fun getCategories(): Flow<List<Category>>
	suspend fun saveCategory(category: Category)
	suspend fun saveCategories(categories: List<Category>)
	suspend fun deleteCategory(categoryId: Long)
	suspend fun getAccountByName(name: String): Account
	suspend fun getCategoryByName(name: String): Category?
	suspend fun getCategoryById(id: Long): Category?
	suspend fun getAllTransactions(): List<Transaction>
	suspend fun getTransactionsForAccount(accountId: Long): List<Transaction>
	suspend fun getTransactionById(id: Long): Transaction
	suspend fun updateCategoryIdsForTransactions(oldCategoryId: Long, newCategoryId: Long?)
	suspend fun updateParentCategoryIds(oldParentCategoryId: Long, newParentCategoryId: Long?)
	suspend fun countTransactionsForCategory(categoryId: Long): Long
	suspend fun getAccountById(id: Long): Account
	fun pagedTransactions(): Flow<PagingData<TransactionListItem>>
	suspend fun deleteAccount(id: Long)
	suspend fun savePlace(place: Place)
	suspend fun getPlaces(): List<Place>
}