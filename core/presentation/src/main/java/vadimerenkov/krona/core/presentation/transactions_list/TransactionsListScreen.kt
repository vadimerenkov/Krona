package vadimerenkov.krona.core.presentation.transactions_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import org.koin.compose.viewmodel.koinViewModel
import vadimerenkov.krona.core.domain.TransactionListItem
import vadimerenkov.krona.core.presentation.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun TransactionsListScreen(
	onTransactionClick: (Long) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: TransactionsListViewModel = koinViewModel()
) {
	TransactionsListScreenRoot(
		state = viewModel.state,
		onAction = { action ->
			when (action) {
				is TransactionsListAction.OnTransactionClick -> {
					onTransactionClick(action.id)
				}
			}
			viewModel.onAction(action)
		},
		modifier = modifier,
		transactions = viewModel.transactions.collectAsLazyPagingItems()
	)
}

@Composable
private fun TransactionsListScreenRoot(
	state: TransactionsListState,
	onAction: (TransactionsListAction) -> Unit,
	modifier: Modifier = Modifier,
	transactions: LazyPagingItems<TransactionListItem>
) {
	if (transactions.loadState.refresh == LoadState.Loading) {
		CircularProgressIndicator(
			modifier = Modifier
				.fillMaxSize()
				.wrapContentSize()
		)
	}

	if (transactions.itemCount == 1) {
		Text(
			text = stringResource(R.string.add_first_transaction),
			color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
			modifier = Modifier
				.fillMaxSize()
				.wrapContentSize()
		)
	} else {
		LazyColumn(
			verticalArrangement = Arrangement.spacedBy(16.dp),
			contentPadding = PaddingValues(16.dp),
			modifier = Modifier
				.fillMaxSize()
		) {
			val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
			items(
				count = transactions.itemCount,
				key = { index ->
					val item = transactions.peek(index)
					when (item) {
						is TransactionListItem.DateHeader -> item.date ?: index
						is TransactionListItem.TransactionItem -> item.transaction.id
						null -> index
					}
				}
			) { index ->
				val item = transactions[index]
				when (item) {
					is TransactionListItem.DateHeader -> {
						Text(
							text = formatter.format(item.date ?: LocalDate.now())
						)
					}

					is TransactionListItem.TransactionItem -> {
						TransactionItem(
							transaction = item.transaction,
							onClick = {
								onAction(TransactionsListAction.OnTransactionClick(item.transaction.id))
							}
						)
					}

					null -> {
						CircularProgressIndicator()
					}
				}
			}
		}
	}
}