package vadimerenkov.krona.core.presentation.import

import java.io.File

sealed interface ImportAction {
	data object OnZenMoneyClick: ImportAction
	data object OnCashewClick: ImportAction
	data class ImportFileChosen(val file: File): ImportAction
}