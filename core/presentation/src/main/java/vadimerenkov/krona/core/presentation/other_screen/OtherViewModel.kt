package vadimerenkov.krona.core.presentation.other_screen

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vadimerenkov.krona.core.domain.csv.CsvExporter

class OtherViewModel(
	private val csvExporter: CsvExporter
): ViewModel() {

	fun exportCsv(uri: Uri, context: Context) {
		val outputStream = context.contentResolver.openOutputStream(uri)
		if (outputStream != null) {
			viewModelScope.launch {
				csvExporter.createCsvFile(outputStream)
			}
		}
	}
}