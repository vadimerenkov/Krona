package vadimerenkov.krona.core.data.csv.cashew

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.csv.CSVFormat
import vadimerenkov.krona.core.domain.Account
import vadimerenkov.krona.core.domain.Category
import vadimerenkov.krona.core.domain.Transaction
import vadimerenkov.krona.core.domain.TransactionsRepository
import vadimerenkov.krona.core.domain.csv.CashewImporter
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Currency
import kotlin.random.Random

class CashewCsvImporter(
	private val repository: TransactionsRepository,
	private val applicationScope: CoroutineScope
): CashewImporter {

	override suspend fun readFromFile(file: File) {
		withContext(Dispatchers.IO) {
			file.bufferedReader().use { reader ->
				val parser = CSVFormat.DEFAULT.builder()
					.get()
					.parse(reader)

				val transactions = mutableListOf<ImportedTransaction>()
				val accounts = mutableSetOf<ImportedAccount>()
				val categories = mutableSetOf<String>()

				parser.forEach { row ->
					if (row.recordNumber > 1) {
						val account = ImportedAccount(row[0], row[3])
						val category = row[9]

						transactions.add(ImportedTransaction(
							date = row[6],
							category = category,
							account = account,
							amount = row[1],
							note = row[4]
						))
						accounts.add(account)
						if (category.isNotBlank()) {
							categories.add(category)
						}
					}
				}

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
	val date: String,
	val category: String,
	val account: ImportedAccount,
	val amount: String,
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
	val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
	return Transaction(
		account = account,
		category = category,
		amount = amount.toDouble(),
		note = note.ifBlank { null },
		timestamp = LocalDateTime.parse(date, formatter).atZone(ZoneId.systemDefault()),
	)
}