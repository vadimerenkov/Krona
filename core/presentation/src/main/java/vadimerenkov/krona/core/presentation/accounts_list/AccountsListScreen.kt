package vadimerenkov.krona.core.presentation.accounts_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import vadimerenkov.krona.core.presentation.R
import java.text.NumberFormat

@Composable
fun AccountsListScreen(
	viewModel: AccountsListViewModel = koinViewModel(),
	onAccountClick: (Long) -> Unit
) {
	AccountsListScreenRoot(
		state = viewModel.state,
		onAction = { action ->
			when (action) {
				is AccountsListAction.OnAccountClick -> onAccountClick(action.id)
				else -> Unit
			}
			viewModel.onAction(action)
		}
	)
}

@Composable
private fun AccountsListScreenRoot(
	state: AccountsListState,
	onAction: (AccountsListAction) -> Unit
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier
			.fillMaxSize()
			.padding(top = 16.dp)
	) {
		val formatter = remember { NumberFormat.getInstance() }
		formatter.maximumFractionDigits = 2
		formatter.minimumFractionDigits = 2
		Text(
			text = "${formatter.format(state.totalBalance)} ${state.defaultCurrency.symbol}",
			fontSize = 36.sp
		)
		LazyColumn(
			horizontalAlignment = Alignment.CenterHorizontally,
			contentPadding = PaddingValues(16.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp),
			modifier = Modifier
				.fillMaxSize()
		) {
			val accounts = state.accounts.filter { !it.isArchived }
			val archivedAccounts = state.accounts.filter { it.isArchived }
			items(
				items = accounts,
				key = { it.id }
			) { account ->
				AccountItem(
					account = account,
					onClick = {
						onAction(AccountsListAction.OnAccountClick(account.id))
					},
					modifier = Modifier
						.widthIn(max = 500.dp)
				)
			}
			if (archivedAccounts.isNotEmpty()) {
				item {
					Text(
						text = if (state.showArchivedAccounts) stringResource(R.string.hide_archived_accounts) else stringResource(
							R.string.show_archived_accounts
						),
						modifier = Modifier
							.clickable {
								onAction(AccountsListAction.OnShowArchivedClick)
							}
							.padding(16.dp)
					)
				}
			}
			if (state.showArchivedAccounts) {
				items(
					items = archivedAccounts,
					key = { it.id }
				) { account ->
					AccountItem(
						account = account,
						onClick = {
							onAction(AccountsListAction.OnAccountClick(account.id))
						},
						modifier = Modifier
							.widthIn(max = 500.dp)
							.animateItem()
					)
				}
			}
			item {
				Spacer(modifier = Modifier.height(48.dp))
			}
		}
	}
}