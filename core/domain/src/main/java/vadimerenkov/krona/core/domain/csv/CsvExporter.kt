package vadimerenkov.krona.core.domain.csv

import java.io.OutputStream

interface CsvExporter {

	suspend fun createCsvFile(outputStream: OutputStream)
}