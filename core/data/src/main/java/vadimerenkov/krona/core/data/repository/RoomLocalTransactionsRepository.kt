package vadimerenkov.krona.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import vadimerenkov.krona.core.data.dao.AccountsDao
import vadimerenkov.krona.core.data.dao.CategoriesDao
import vadimerenkov.krona.core.data.dao.PersonDao
import vadimerenkov.krona.core.data.dao.PlacesDao
import vadimerenkov.krona.core.data.dao.TransactionsDao
import vadimerenkov.krona.core.data.entities.CategoryData
import vadimerenkov.krona.core.data.mappers.toAccount
import vadimerenkov.krona.core.data.mappers.toAccountWithAmount
import vadimerenkov.krona.core.data.mappers.toCategory
import vadimerenkov.krona.core.data.mappers.toData
import vadimerenkov.krona.core.data.mappers.toPerson
import vadimerenkov.krona.core.data.mappers.toPlace
import vadimerenkov.krona.core.data.mappers.toTransaction
import vadimerenkov.krona.core.domain.Account
import vadimerenkov.krona.core.domain.Category
import vadimerenkov.krona.core.domain.Person
import vadimerenkov.krona.core.domain.Place
import vadimerenkov.krona.core.domain.Transaction
import vadimerenkov.krona.core.domain.TransactionListItem
import vadimerenkov.krona.core.domain.TransactionsRepository

class RoomLocalTransactionsRepository(
	private val accountsDao: AccountsDao,
	private val transactionsDao: TransactionsDao,
	private val categoriesDao: CategoriesDao,
	private val personDao: PersonDao,
	private val placesDao: PlacesDao
): TransactionsRepository {

	override suspend fun saveAccount(account: Account) {
		accountsDao.saveAccount(account.toData())
	}

	override suspend fun saveAccounts(accounts: List<Account>) {
		accountsDao.saveAccounts(accounts.map { it.toData() })
	}

	override suspend fun saveTransaction(transaction: Transaction) {
		transactionsDao.saveTransaction(transaction.toData())
	}

	override suspend fun saveTransactions(transactions: List<Transaction>) {
		transactionsDao.saveTransactions(transactions.map { it.toData() })
	}

	override suspend fun deleteTransaction(id: Long) {
		transactionsDao.deleteTransaction(id)
	}

	override suspend fun savePerson(person: Person) {
		personDao.savePerson(person.toData())
	}

	override suspend fun savePersons(persons: List<Person>) {
		personDao.savePersons(persons.map { it.toData() })
	}

	override fun getPersons(): Flow<List<Person>> {
		return personDao.getPersons().map { persons ->
			persons.map { it.toPerson() }
		}
	}

	override suspend fun getPersonByName(name: String): Person? {
		return personDao.getPersonByName(name)?.toPerson()
	}

	override fun getAccounts(): Flow<List<Account>> {
		return accountsDao.getAccountsWithAmounts().map { accounts ->
			accounts.map { it.toAccount() }
		}
	}

	override suspend fun getDefaultAccount(): Account {
		val accountData = accountsDao.getDefaultAccount()
		val amount = accountsDao.getAmountForAccount(accountData.id) + accountData.initialAmount
		return accountData.toAccountWithAmount(amount).toAccount()
	}

	override fun getCategories(): Flow<List<Category>> {
		return categoriesDao.getCategories().map { categories ->
			buildCategoryTree(categories)
		}
	}

	override suspend fun saveCategory(category: Category) {
		categoriesDao.saveCategory(category.toData())
	}

	override suspend fun saveCategories(categories: List<Category>) {
		categoriesDao.saveCategories(categories.map { it.toData() })
	}

	override suspend fun deleteCategory(categoryId: Long) {
		categoriesDao.deleteCategory(categoryId)
	}

	override suspend fun getAccountByName(name: String): Account {
		return accountsDao.getAccountByName(name).toAccountWithAmount(0.0).toAccount()
	}

	override suspend fun getCategoryByName(name: String): Category? {
		return categoriesDao.getCategoryByName(name)?.toCategory()
	}

	override suspend fun getCategoryById(id: Long): Category? {
		val data = categoriesDao.getCategoryById(id)
		val parentCategory = if (data?.parentCategoryId != null) categoriesDao.getCategoryById(data.parentCategoryId) else null
		return data?.toCategory(parentCategory?.toCategory())
	}

	override suspend fun getAllTransactions(): List<Transaction> {
		return transactionsDao.getAllTransactions().first().map { transactionData ->
			val account = accountsDao.getAccountById(transactionData.accountId).toAccountWithAmount(0.0).toAccount()
			val category = categoriesDao.getCategoryById(transactionData.categoryId)?.toCategory()
			val payees = Json.decodeFromString<List<String>>(transactionData.payees).map { name ->
				personDao.getPersonByName(name)!!.toPerson()
			}
			transactionData.toTransaction(account, category, payees)
		}
	}

	override suspend fun getTransactionsForAccount(accountId: Long): List<Transaction> {
		val account = accountsDao.getAccountById(accountId).toAccountWithAmount(0.0).toAccount()
		return transactionsDao.getTransactionsForAccount(accountId).map { data ->
			val category = categoriesDao.getCategoryById(data.categoryId)?.toCategory()
			val payees = Json.decodeFromString<List<String>>(data.payees).map { name ->
				personDao.getPersonByName(name)!!.toPerson()
			}
			data.toTransaction(account, category, payees)
		}
	}

	override suspend fun getTransactionById(id: Long): Transaction {
		val data = transactionsDao.getTransactionById(id)
		val account = accountsDao.getAccountById(data.accountId).toAccountWithAmount(0.0).toAccount()
		val category = categoriesDao.getCategoryById(data.categoryId)?.toCategory()
		val payees = Json.decodeFromString<List<String>>(data.payees).map { name ->
			personDao.getPersonByName(name)!!.toPerson()
		}
		return data.toTransaction(account, category, payees)
	}

	override suspend fun updateCategoryIdsForTransactions(oldCategoryId: Long, newCategoryId: Long?) {
		transactionsDao.updateCategoryIds(oldCategoryId, newCategoryId)
	}

	override suspend fun updateParentCategoryIds(
		oldParentCategoryId: Long,
		newParentCategoryId: Long?
	) {
		categoriesDao.updateParentCategoryIds(oldParentCategoryId, newParentCategoryId)
	}

	override suspend fun countTransactionsForCategory(categoryId: Long): Long {
		return transactionsDao.countTransactionsForCategory(categoryId)
	}

	override suspend fun getAccountById(id: Long): Account {
		val amount = accountsDao.getAmountForAccount(id)
		val data = accountsDao.getAccountById(id)
		return data.toAccountWithAmount(amount + data.initialAmount).toAccount()
	}

	override fun pagedTransactions(): Flow<PagingData<TransactionListItem>> {
		return Pager(
			config = PagingConfig(
				pageSize = 30
			),
			pagingSourceFactory = {
				transactionsDao.pagingSource()
			}
		)
			.flow
			.map { pagingData ->
				pagingData
					.map { transaction ->
						val amount = accountsDao.getAmountForAccount(transaction.accountId)
						val account = accountsDao.getAccountById(transaction.accountId).toAccountWithAmount(amount).toAccount()
						val categoryData = categoriesDao.getCategoryById(transaction.categoryId)?.toCategory()
						val payees = Json.decodeFromString<List<String>>(transaction.payees).map { name ->
							personDao.getPersonByName(name)!!.toPerson()
						}
						val tr = transaction.toTransaction(account, categoryData, payees)
						TransactionListItem.TransactionItem(tr)
					}.insertSeparators { before, after ->
						when {
							before == null -> TransactionListItem.DateHeader(after?.transaction?.timestamp?.toLocalDate())
							after == null -> null
							after.transaction.timestamp.toLocalDate() != before.transaction.timestamp.toLocalDate() -> TransactionListItem.DateHeader(
								after.transaction.timestamp.toLocalDate()
							)
							else -> null
						}
					}
			}
	}

	override suspend fun deleteAccount(id: Long) {
		accountsDao.deleteAccount(id)
	}

	override suspend fun savePlace(place: Place) {
		placesDao.savePlace(place.toData())
	}

	override suspend fun getPlaces(): List<Place> {
		return placesDao.getPlaces().map { it.toPlace() }
	}

	private fun buildCategoryTree(categories: List<CategoryData>, parentId: Long? = null): List<Category> {
		return categories
			.filter { it.parentCategoryId == parentId }
			.map { category ->
				val parentCategory = categories.find { it.id == category.parentCategoryId }
				category.toCategory(parentCategory?.toCategory()).copy(childCategories = buildCategoryTree(categories, category.id))
			}
	}
}