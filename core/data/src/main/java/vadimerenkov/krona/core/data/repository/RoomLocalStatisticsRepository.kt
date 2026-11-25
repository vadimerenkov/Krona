package vadimerenkov.krona.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import vadimerenkov.krona.core.data.dao.AccountsDao
import vadimerenkov.krona.core.data.dao.CategoriesDao
import vadimerenkov.krona.core.data.dao.PersonDao
import vadimerenkov.krona.core.data.dao.TransactionsDao
import vadimerenkov.krona.core.data.entities.TransactionData
import vadimerenkov.krona.core.data.mappers.toAccount
import vadimerenkov.krona.core.data.mappers.toAccountWithAmount
import vadimerenkov.krona.core.data.mappers.toPerson
import vadimerenkov.krona.core.data.mappers.toTransaction
import vadimerenkov.krona.core.domain.Category
import vadimerenkov.krona.core.domain.ExchangeRatesRepository
import vadimerenkov.krona.core.domain.StatisticsRepository
import vadimerenkov.krona.core.domain.Transaction
import vadimerenkov.krona.core.domain.TransactionType
import vadimerenkov.krona.core.domain.TransactionsRepository
import vadimerenkov.krona.core.domain.settings.Settings
import vadimerenkov.krona.core.domain.util.flatten
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Currency
import kotlin.math.absoluteValue

class RoomLocalStatisticsRepository(
	private val transactionsDao: TransactionsDao,
	private val accountsDao: AccountsDao,
	private val personDao: PersonDao,
	private val categoriesDao: CategoriesDao,
	private val exchangeRatesRepository: ExchangeRatesRepository,
	private val transactionsRepository: TransactionsRepository,
	private val settings: Settings
): StatisticsRepository {

	override suspend fun get6MonthsStatsForTransaction(transaction: Transaction): List<Double> {
		val currentMonth = YearMonth.now()
		return (0 until 6)
			.map { i ->
				val month = currentMonth.minusMonths(i.toLong())
				val startOfMonth = month.atDay(1).atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond()
				val endOfMonth = month.atEndOfMonth().atTime(23, 59).atZone(ZoneId.systemDefault()).toEpochSecond()
				if (transaction.category == null) {
					transactionsDao.getTransactionsForDateWithoutCategory(startOfMonth, endOfMonth)
				} else {
					transactionsDao.getTransactionsForDateForCategory(startOfMonth, endOfMonth, transaction.category!!.id)
				}
			}
			.map { transactionsForMonth ->
				val isExpense = transaction.amount < 0.0
				transactionsForMonth
					.filter { it.type != TransactionType.TRANSFER }
					.filter {
						if (isExpense) it.amount < 0.0 else it.amount > 0.0
					}.sumOf { transactionData ->
						val amounts =
							Json.decodeFromString<List<Double>>(transactionData.splitAmounts)
						val amount = if (amounts.isNotEmpty()) {
							Json.decodeFromString<List<String>>(transactionData.payees)
								.map { name ->
									personDao.getPersonByName(name)!!
								}
								.zip(amounts) { person, amount ->
									if (person.isMe) {
										amount
									} else {
										null
									}
								}.filterNotNull().firstOrNull()
						} else {
							transactionData.amount
						}
						val amountInDefaultCurrency = exchangeRatesRepository.getAmountInCurrency(amount ?: 0.0, transaction.account.currency, settings.state.value.defaultCurrency)
						amountInDefaultCurrency ?: 0.0
					}
					.absoluteValue
			}
			.reversed()

	}

	override fun getMonthStatsForAllCategories(): Flow<Map<Category?, Double>> {
		val currentMonth = YearMonth.now()
		val start = currentMonth.atDay(1).atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond()
		val end = currentMonth.atEndOfMonth().atTime(23, 59).atZone(ZoneId.systemDefault()).toEpochSecond()
		val t = transactionsDao.getAllTransactionsForDate(start, end)
			.map { list ->
				val categorised = list.groupBy { it.categoryId }
				categorised
			}
			.map { map ->
				map.map { (id, transactions ) ->
					val categories = transactionsRepository.getCategories().first()
					val category = categories.find { it.id == id }
					category to transactions
						.filter { it.amount <= 0 }
						.sumOf { transactionData ->
							val accountCurrency = Currency.getInstance(accountsDao.getCurrencyForAccountId(transactionData.accountId))
							val amountInDefaultCurrency = exchangeRatesRepository.getAmountInCurrency(transactionData.amount, accountCurrency, settings.state.value.defaultCurrency)
							amountInDefaultCurrency ?: 0.0
						}.absoluteValue
				}
					.sortedByDescending { it.second }
					.toMap()
					.filterValues { it != 0.0 }
			}
		return t
	}

	override suspend fun getMonthStatsForCategory(
		monthStart: ZonedDateTime,
		monthEnd: ZonedDateTime,
		parentCategoryId: Long?
	): Map<Category?, List<Transaction>> {
		val start = monthStart.toEpochSecond()
		val end = monthEnd.toEpochSecond()
		val categories = transactionsRepository.getCategories().first().flatten()
		val parentCategory = categories.find { it.id == parentCategoryId }
		println(parentCategory)

		val transactionsForParentCategory = if (parentCategory == null) {
			val t = transactionsDao.getTransactionsForDateWithoutCategory(start, end)
			println(t)
			t
		} else {
			transactionsDao.getTransactionsForDateForCategory(start, end, parentCategory.id)
		}

		val transactionsForChildCategories = if (parentCategory == null) {
			val categoriesWithoutParents = categories.filter { it.parentCategory == null }
			categoriesWithoutParents.map { category ->
				category to category.getTransactionsForChildCategories(start, end)
			}
		} else {
			parentCategory.childCategories.map { category ->
				category to category.getTransactionsForChildCategories(start, end)
			}
		}.toMap()

		val allTransactions = (mapOf(parentCategory to transactionsForParentCategory)) + transactionsForChildCategories

		return allTransactions
			.mapValues { (category, list) ->
				list
					.filter { it.amount <= 0 }
					.map { data ->
						val account = accountsDao.getAccountById(data.accountId).toAccountWithAmount(0.0).toAccount()
						val category = categories.find { it.id == data.categoryId }
						val persons = Json.decodeFromString<List<String>>(data.payees).map { name ->
							personDao.getPersonByName(name)?.toPerson()
						}.filterNotNull()
						data.toTransaction(account, category, persons)
					}
			}
	}

	private suspend fun Category.getTransactionsForChildCategories(start: Long, end: Long): List<TransactionData> {
		val list = mutableListOf<TransactionData>()
		val parentCategoryTransactions = transactionsDao.getTransactionsForDateForCategory(start, end, id)
		list.addAll(parentCategoryTransactions)
		val childrenCategoriesTransactions = childCategories.flatMap { category ->
			category.getTransactionsForChildCategories(start, end)
		}
		list.addAll(childrenCategoriesTransactions)
		return list
	}
}