package vadimerenkov.krona.core.presentation.di

import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import vadimerenkov.krona.core.presentation.account.AccountViewModel
import vadimerenkov.krona.core.presentation.accounts_list.AccountsListViewModel
import vadimerenkov.krona.core.presentation.categories_list.CategoriesListViewModel
import vadimerenkov.krona.core.presentation.debts.DebtsViewModel
import vadimerenkov.krona.core.presentation.edit_account.EditAccountViewModel
import vadimerenkov.krona.core.presentation.edit_category.EditCategoryViewModel
import vadimerenkov.krona.core.presentation.edit_transaction.EditTransactionViewModel
import vadimerenkov.krona.core.presentation.import.ImportViewModel
import vadimerenkov.krona.core.presentation.main.MainViewModel
import vadimerenkov.krona.core.presentation.onboarding.OnboardingViewModel
import vadimerenkov.krona.core.presentation.other_screen.OtherViewModel
import vadimerenkov.krona.core.presentation.piechart.PieChartViewModel
import vadimerenkov.krona.core.presentation.settings.SettingsViewModel
import vadimerenkov.krona.core.presentation.statistics.StatisticsViewModel
import vadimerenkov.krona.core.presentation.transaction.TransactionViewModel
import vadimerenkov.krona.core.presentation.transactions_list.TransactionsListViewModel

val corePresentationModule = module {

	viewModelOf(::TransactionsListViewModel)
	viewModelOf(::AccountsListViewModel)
	viewModelOf(::CategoriesListViewModel)
	viewModelOf(::MainViewModel)
	viewModelOf(::OnboardingViewModel)
	viewModelOf(::DebtsViewModel)
	viewModelOf(::ImportViewModel)
	viewModelOf(::OtherViewModel)
	viewModelOf(::SettingsViewModel)
	viewModelOf(::StatisticsViewModel)
	viewModelOf(::PieChartViewModel)

	viewModel<EditAccountViewModel> { params ->
		EditAccountViewModel(params[0], get())
	}

	viewModel<EditTransactionViewModel> { params ->
		EditTransactionViewModel(params[0], get(), get(), get())
	}

	viewModel<EditCategoryViewModel> { params ->
		EditCategoryViewModel(params[0], get())
	}

	viewModel<AccountViewModel> { params ->
		AccountViewModel(id = params[0], transactionsRepository = get(), get(), get())
	}

	viewModel<TransactionViewModel> { params ->
		TransactionViewModel(id = params[0], get(), get())
	}
}