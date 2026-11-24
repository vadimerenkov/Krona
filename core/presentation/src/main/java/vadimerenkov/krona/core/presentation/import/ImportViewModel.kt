package vadimerenkov.krona.core.presentation.import

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vadimerenkov.krona.core.domain.csv.CashewImporter
import vadimerenkov.krona.core.domain.csv.ZenImporter
import java.io.File

class ImportViewModel(
	private val zenImporter: ZenImporter,
	private val cashewImporter: CashewImporter
): ViewModel() {

	var state by mutableStateOf(ImportState())

	private val channel = Channel<ImportEvent>()
	val events = channel.receiveAsFlow()

	fun onAction(action: ImportAction) {
		when (action) {
			ImportAction.OnZenMoneyClick -> {
				state = state.copy(selectedApp = SelectedApp.ZENMONEY)
			}
			is ImportAction.ImportFileChosen -> {
				viewModelScope.launch {
					readFile(action.file)
					channel.send(ImportEvent.ImportFinished)
				}
			}

			ImportAction.OnCashewClick -> {
				state = state.copy(selectedApp = SelectedApp.CASHEW)
			}
		}
	}

	private suspend fun readFile(file: File) {
		try {
			when (state.selectedApp) {
				SelectedApp.ZENMONEY -> {
					zenImporter.readFromFile(file)
				}
				SelectedApp.CASHEW -> {
					cashewImporter.readFromFile(file)
				}
				null -> {}
			}
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
}