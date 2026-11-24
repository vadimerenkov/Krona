package vadimerenkov.krona.core.presentation.main

data class MainState(
	val currentPage: MainPage = MainPage.TRANSACTIONS,
)

enum class MainPage {
	ACCOUNTS,
	TRANSACTIONS,
	STATISTICS,
	OTHER
}