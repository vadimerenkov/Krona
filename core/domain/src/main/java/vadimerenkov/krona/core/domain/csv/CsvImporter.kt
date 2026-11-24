package vadimerenkov.krona.core.domain.csv

import java.io.File

interface CsvImporter {

	suspend fun readFromFile(file: File)
}