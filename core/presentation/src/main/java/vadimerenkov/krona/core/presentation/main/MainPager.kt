package vadimerenkov.krona.core.presentation.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.koin.compose.viewmodel.koinViewModel
import vadimerenkov.krona.core.presentation.R
import vadimerenkov.krona.core.presentation.accounts_list.AccountsListScreen
import vadimerenkov.krona.core.presentation.other_screen.OtherScreen
import vadimerenkov.krona.core.presentation.statistics.StatisticsScreen
import vadimerenkov.krona.core.presentation.transactions_list.TransactionsListScreen
import vadimerenkov.krona.core.presentation.util.ObserveAsEvents

@Composable
fun MainPager(
	viewModel: MainViewModel = koinViewModel(),
	onEditAccountClick: (Long) -> Unit,
	onEditTransactionClick: (Long) -> Unit,
	startOnboarding: () -> Unit,
	onAccountClick: (Long) -> Unit,
	onTransactionClick: (Long) -> Unit,
	onCategoriesClick: () -> Unit,
	onDebtsClick: () -> Unit,
	onImportClick: () -> Unit,
	onSettingsClick: () -> Unit,
	onPieChartClick: () -> Unit
) {
	ObserveAsEvents(viewModel.events) { event ->
		when (event) {
			MainEvent.StartOnboarding -> startOnboarding()
		}
	}

	MainPagerRoot(
		state = viewModel.state,
		onAction = { action ->
			when (action) {
				MainAction.AddAccount -> onEditAccountClick(0)
				MainAction.AddTransaction -> onEditTransactionClick(0)
				MainAction.OnCategoriesClick -> onCategoriesClick()
				MainAction.OnDebtsClick -> onDebtsClick()
				MainAction.OnImportClick -> onImportClick()
				MainAction.OnSettingsClick -> onSettingsClick()
				MainAction.OnPieChartClick -> onPieChartClick()
				is MainAction.OnAccountClick -> onAccountClick(action.id)
				is MainAction.OnTransactionClick -> onTransactionClick(action.id)
				else -> Unit
			}
			viewModel.onAction(action)
		}
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainPagerRoot(
	state: MainState,
	onAction: (MainAction) -> Unit,
) {
	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Text(
						text = when (state.currentPage) {
							MainPage.ACCOUNTS -> stringResource(R.string.accounts)
							MainPage.TRANSACTIONS -> stringResource(R.string.transactions)
							MainPage.STATISTICS -> stringResource(R.string.statistics)
							MainPage.OTHER -> stringResource(R.string.other)
						}
					)
				},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.primaryContainer
				)
			)
		},
		bottomBar = {
			NavigationBar(
				containerColor = MaterialTheme.colorScheme.primaryContainer,
				contentColor = MaterialTheme.colorScheme.onPrimaryContainer
			) {
				NavigationBarItem(
					selected = state.currentPage == MainPage.ACCOUNTS,
					onClick = { onAction(MainAction.ChangePage(MainPage.ACCOUNTS)) },
					icon = {
						Icon(
							imageVector = Icons.Default.AccountBalanceWallet,
							contentDescription = null
						)
					},
					label = {
						Text(
							text = stringResource(R.string.accounts)
						)
					},
					colors = NavigationBarItemDefaults.colors(
						indicatorColor = MaterialTheme.colorScheme.onPrimaryContainer,
						selectedIconColor = MaterialTheme.colorScheme.onPrimary
					)
				)
				NavigationBarItem(
					selected = state.currentPage == MainPage.TRANSACTIONS,
					onClick = { onAction(MainAction.ChangePage(MainPage.TRANSACTIONS)) },
					icon = {
						Icon(
							imageVector = Icons.AutoMirrored.Filled.List,
							contentDescription = null
						)
					},
					label = {
						Text(
							text = stringResource(R.string.transactions)
						)
					},
					colors = NavigationBarItemDefaults.colors(
						indicatorColor = MaterialTheme.colorScheme.onPrimaryContainer,
						selectedIconColor = MaterialTheme.colorScheme.onPrimary
					)
				)
				NavigationBarItem(
					selected = state.currentPage == MainPage.STATISTICS,
					onClick = { onAction(MainAction.ChangePage(MainPage.STATISTICS)) },
					icon = {
						Icon(
							imageVector = Icons.Default.BarChart,
							contentDescription = null
						)
					},
					label = {
						Text(
							text = stringResource(R.string.statistics)
						)
					},
					colors = NavigationBarItemDefaults.colors(
						indicatorColor = MaterialTheme.colorScheme.onPrimaryContainer,
						selectedIconColor = MaterialTheme.colorScheme.onPrimary
					)
				)
				NavigationBarItem(
					selected = state.currentPage == MainPage.OTHER,
					onClick = { onAction(MainAction.ChangePage(MainPage.OTHER)) },
					icon = {
						Icon(
							imageVector = Icons.Default.MoreHoriz,
							contentDescription = null
						)
					},
					label = {
						Text(
							text = stringResource(R.string.other)
						)
					},
					colors = NavigationBarItemDefaults.colors(
						indicatorColor = MaterialTheme.colorScheme.onPrimaryContainer,
						selectedIconColor = MaterialTheme.colorScheme.onPrimary
					)
				)
			}
		},
		floatingActionButton = {
			when (state.currentPage) {
				MainPage.ACCOUNTS,
				MainPage.TRANSACTIONS -> {
					FloatingActionButton(
						onClick = {
							when (state.currentPage) {
								MainPage.ACCOUNTS -> onAction(MainAction.AddAccount)
								MainPage.TRANSACTIONS -> onAction(MainAction.AddTransaction)
							}
						}
					) {
						Icon(
							imageVector = Icons.Default.Add,
							contentDescription = when (state.currentPage) {
								MainPage.ACCOUNTS -> stringResource(R.string.new_account)
								MainPage.TRANSACTIONS -> stringResource(R.string.new_transaction)
							}
						)
					}
				}
				else -> {}
			}

		}
	) { innerPadding ->
		val pagerState = rememberPagerState(initialPage = 1) { 4 }

		LaunchedEffect(pagerState.currentPage) {
			onAction(MainAction.ChangePage(MainPage.entries[pagerState.currentPage]))
		}

		LaunchedEffect(state.currentPage) {
			pagerState.animateScrollToPage(state.currentPage.ordinal)
		}

		HorizontalPager(
			state = pagerState,
			modifier = Modifier
				.padding(innerPadding)
				.fillMaxSize()
		) { page ->
			when (page) {
				0 -> {
					AccountsListScreen(
						onAccountClick = {
							onAction(MainAction.OnAccountClick(it))
						}
					)
				}
				1 -> {
					TransactionsListScreen(
						onTransactionClick = { id ->
							onAction(MainAction.OnTransactionClick(id))
						}
					)
				}
				2 -> {
					StatisticsScreen(
						onPieChartClick = {
							onAction(MainAction.OnPieChartClick)
						}
					)
				}
				3 -> {
					OtherScreen(
						onCategoriesClick = {
							onAction(MainAction.OnCategoriesClick)
						},
						onDebtsClick = {
							onAction(MainAction.OnDebtsClick)
						},
						onImportClick = {
							onAction(MainAction.OnImportClick)
						},
						onSettingsClick = {
							onAction(MainAction.OnSettingsClick)
						}
					)
				}
			}
		}

	}
}