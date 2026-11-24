package vadimerenkov.krona.core.data.csv.zen

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.csv.CSVFormat
import vadimerenkov.krona.core.domain.Account
import vadimerenkov.krona.core.domain.Category
import vadimerenkov.krona.core.domain.Transaction
import vadimerenkov.krona.core.domain.TransactionType
import vadimerenkov.krona.core.domain.TransactionsRepository
import vadimerenkov.krona.core.domain.csv.ZenImporter
import java.io.File
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Currency
import kotlin.random.Random

class ZenCsvImporter(
	private val repository: TransactionsRepository,
	private val applicationScope: CoroutineScope
): ZenImporter {

	override suspend fun readFromFile(file: File) {
		withContext(Dispatchers.IO) {
			file.bufferedReader().use { reader ->

				val parser = CSVFormat.DEFAULT.builder()
					.setDelimiter(";")
					.build()
					.parse(reader)

				val transactions = mutableListOf<ImportedTransaction>()
				val accounts = mutableSetOf<ImportedAccount>()
				val categories = mutableSetOf<String>()

				parser.forEach { row ->
					if (row.recordNumber > 1) {
						val account = ImportedAccount(row[4], row[6])
						val account2 = ImportedAccount(row[7], row[9])
						val category = row[1].substringAfterLast("/ ").substringAfterLast(", ")
						val isTransfer = account != account2
						val isIncome = row[8].toDoubleOrNull() != 0.0 && !isTransfer

						transactions.add(
							ImportedTransaction(
								date = row[0],
								category = category,
								place = row[2],
								account = account,
								amount = if (isIncome) row[8] else "-${row[5]}",
								currency = row[6],
								outgoingAccount = row[7],
								income = row[8],
								outgoingCurrency = row[9],
								isTransfer = isTransfer,
								note = row[3]
							)
						)
						if (isTransfer) {
							transactions.add(
								ImportedTransaction(
									date = row[0],
									place = row[2],
									account = account2,
									category = category,
									amount = row[8],
									currency = row[9],
									outgoingAccount = row[7],
									outgoingCurrency = row[9],
									income = row[8],
									isTransfer = true,
									note = row[3]
								)
							)
						}
						accounts.add(account)
						accounts.add(account2)
						if (category.isNotBlank()) {
							categories.add(category)
						}
					} else {
						println(row)
					}
				}
				println("Number of accounts: ${accounts.size}")
				println("Number of categories: ${categories.size}")

				val dbAccounts = accounts.map { it.toAccount() }
				val dbCategories = categories.map { name ->
					Category(
						id = 0,
						name = name,
						color = Random.nextInt(),
					)
				}

				applicationScope.launch {
					repository.saveAccounts(dbAccounts)
					repository.saveCategories(dbCategories)

					val dbTransactions = transactions.map { imported ->
						val account = repository.getAccountByName(imported.account.name)
						val category = repository.getCategoryByName(imported.category)
						imported.toTransaction(account, category)
					}

					repository.saveTransactions(dbTransactions)
				}
			}
		}
	}
}

private data class ImportedTransaction(
	val place: String,
	val date: String,
	val category: String,
	val account: ImportedAccount,
	val amount: String,
	val currency: String,
	val outgoingAccount: String,
	val income: String,
	val outgoingCurrency: String,
	val isTransfer: Boolean,
	val note: String
)

private data class ImportedAccount(
	val name: String,
	val currency: String
)

private fun ImportedAccount.toAccount(): Account {
	return Account(
		id = 0,
		name = name,
		currency = Currency.getInstance(currency)
	)
}

private fun ImportedTransaction.toTransaction(account: Account, category: Category?): Transaction {
	val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
	return Transaction(
		place = place.ifBlank { null },
		account = account,
		category = category,
		amount = amount.toDouble(),
		note = note.ifBlank { null },
		timestamp = LocalDate.parse(date, formatter).atTime(0, 0).atZone(ZoneId.systemDefault()),
		type = if (isTransfer) TransactionType.TRANSFER else TransactionType.INCOME_EXPENSE
	)
}