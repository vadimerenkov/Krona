package vadimerenkov.krona.core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import vadimerenkov.krona.core.presentation.account.AccountScreen
import vadimerenkov.krona.core.presentation.account.AccountViewModel
import vadimerenkov.krona.core.presentation.categories_list.CategoriesListScreen
import vadimerenkov.krona.core.presentation.debts.DebtsScreen
import vadimerenkov.krona.core.presentation.edit_account.EditAccountScreen
import vadimerenkov.krona.core.presentation.edit_account.EditAccountViewModel
import vadimerenkov.krona.core.presentation.edit_category.EditCategoryScreen
import vadimerenkov.krona.core.presentation.edit_category.EditCategoryViewModel
import vadimerenkov.krona.core.presentation.edit_transaction.EditTransactionScreen
import vadimerenkov.krona.core.presentation.edit_transaction.EditTransactionViewModel
import vadimerenkov.krona.core.presentation.import.ImportScreen
import vadimerenkov.krona.core.presentation.main.MainPager
import vadimerenkov.krona.core.presentation.onboarding.OnboardingScreen
import vadimerenkov.krona.core.presentation.piechart.PieChartScreen
import vadimerenkov.krona.core.presentation.settings.SettingsScreen
import vadimerenkov.krona.core.presentation.transaction.TransactionScreen
import vadimerenkov.krona.core.presentation.transaction.TransactionViewModel

@Composable
fun MainNavigationRoot() {
	val backstack = rememberNavBackStack(MainPager)

	NavDisplay(
		backStack = backstack,
		entryDecorators = listOf(
			rememberSaveableStateHolderNavEntryDecorator(),
			rememberViewModelStoreNavEntryDecorator()
		),
		entryProvider = entryProvider {
			entry<MainPager> {
				MainPager(
					onEditAccountClick = { id ->
						backstack.add(EditAccount(id))
					},
					onEditTransactionClick = { id ->
						backstack.add(EditTransaction(id))
					},
					startOnboarding = {
						backstack.clear()
						backstack.add(Onboarding)
					},
					onAccountClick = {
						backstack.add(Account(it))
					},
					onTransactionClick = {
						backstack.add(Transaction(it))
					},
					onCategoriesClick = {
						backstack.add(Categories)
					},
					onDebtsClick = {
						backstack.add(Debts)
					},
					onImportClick = {
						backstack.add(Import)
					},
					onSettingsClick = {
						backstack.add(Settings)
					},
					onPieChartClick = {
						backstack.add(PieChart)
					}
				)
			}
			entry<EditAccount> {
				EditAccountScreen(
					viewModel = koinViewModel<EditAccountViewModel> {
						parametersOf(it.id)
					},
					onBackClick = {
						backstack.removeLastOrNull()
					}
				)
			}
			entry<EditTransaction> {
				EditTransactionScreen(
					viewModel = koinViewModel<EditTransactionViewModel> {
						parametersOf(it.id)
					},
					onBackClick = {
						backstack.removeLastOrNull()
					}
				)
			}
			entry<EditCategory> {
				EditCategoryScreen(
					viewModel = koinViewModel<EditCategoryViewModel> {
						parametersOf(it.id)
					},
					onBackClick = {
						backstack.removeLastOrNull()
					}
				)
			}
			entry<Onboarding> {
				OnboardingScreen(
					goToMainScreen = {
						backstack.clear()
						backstack.add(MainPager)
					}
				)
			}
			entry<Account> {
				AccountScreen(
					onTransactionClick = { id ->
						backstack.add(Transaction(id))
					},
					onBackClick = {
						backstack.removeLastOrNull()
					},
					onEditClick = { id ->
						backstack.add(EditAccount(id ?: 0))
					},
					viewModel = koinViewModel<AccountViewModel> {
						parametersOf(it.id)
					}
				)
			}
			entry<Transaction> {
				TransactionScreen(
					viewModel = koinViewModel<TransactionViewModel> {
						parametersOf(it.id)
					},
					onBackClick = {
						backstack.removeLastOrNull()
					},
					onEditClick = { id ->
						backstack.add(EditTransaction(id ?: 0))
					}
				)
			}
			entry<Categories> {
				CategoriesListScreen(
					onAddCategoryClick = {
						backstack.add(EditCategory(0))
					},
					onEditCategoryClick = {
						backstack.add(EditCategory(it.id))
					}
				)
			}
			entry<Debts> {
				DebtsScreen()
			}
			entry<Import> {
				ImportScreen(
					onBackClick = {
						backstack.removeLastOrNull()
					}
				)
			}
			entry<Settings> {
				SettingsScreen()
			}
			entry<PieChart> {
				PieChartScreen()
			}
		}
	)
}