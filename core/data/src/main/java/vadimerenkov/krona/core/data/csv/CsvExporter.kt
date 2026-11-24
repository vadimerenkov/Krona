package vadimerenkov.krona.core.data.csv

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import vadimerenkov.krona.core.domain.TransactionsRepository
import vadimerenkov.krona.core.domain.csv.CsvExporter
import java.io.OutputStream
import java.time.format.DateTimeFormatter

class CsvExporter(
	private val repository: TransactionsRepository
): CsvExporter {

	override suspend fun createCsvFile(outputStream: OutputStream) {
		withContext(Dispatchers.IO) {
			val csv = CSVFormat.DEFAULT.builder()
				.setHeader(
					"date",
					"amount",
					"account",
					"category",
					"place",
					"type",
					"payees",
					"splitAmounts"
				)
				.get()

			outputStream.use { stream ->
				stream.bufferedWriter().use { writer ->
					val printer = CSVPrinter(writer, csv)
					val transactions = repository.getAllTransactions()
					val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
					transactions.forEach { transaction ->
						val payees = if (transaction.payees.isEmpty()) "" else transaction.payees.joinToString(";", "[", "]") { it.name }
						val splitAmounts = if (transaction.splitAmounts.isEmpty()) "" else transaction.splitAmounts.joinToString(";", "[", "]")
						printer.printRecord(
							formatter.format(transaction.timestamp),
							transaction.amount.toString(),
							transaction.account.name,
							transaction.category?.name ?: "",
							transaction.place ?: "",
							transaction.type.toString(),
							payees,
							splitAmounts
						)
					}
				}
			}
		}
	}
}