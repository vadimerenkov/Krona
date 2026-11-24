package vadimerenkov.krona.core.presentation.import

data class ImportState(
	val selectedApp: SelectedApp? = null
)

enum class SelectedApp {
	ZENMONEY,
	CASHEW
}