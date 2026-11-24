package vadimerenkov.krona.core.presentation.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.koin.compose.viewmodel.koinViewModel
import vadimerenkov.krona.core.domain.Account
import vadimerenkov.krona.core.presentation.R
import vadimerenkov.krona.core.presentation.transactions_list.TransactionItem
import vadimerenkov.krona.core.presentation.util.ObserveAsEvents
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Currency

@Composable
fun AccountScreen(
	onTransactionClick: (Long) -> Unit,
	onBackClick: () -> Unit,
	onEditClick: (Long?) -> Unit,
	viewModel: AccountViewModel = koinViewModel()
) {
	ObserveAsEvents(viewModel.events) { event ->
		when (event) {
			AccountEvent.FinishedSaving -> onBackClick()
		}
	}
	AccountRoot(
		state = viewModel.state,
		onAction = { action ->
			when (action) {
				is AccountAction.OnTransactionClick -> onTransactionClick(action.id)
				AccountAction.OnEditClick -> onEditClick(viewModel.state.account?.id)
				else -> Unit
			}
			viewModel.onAction(action)
		}
	)
}

@Composable
private fun AccountRoot(
	state: AccountState,
	onAction: (AccountAction) -> Unit
) {
	var changeBalanceDialogOpen by remember { mutableStateOf(false) }
	var deleteDialogOpen by remember { mutableStateOf(false) }
	var archiveDialogOpen by remember { mutableStateOf(false) }

	if (changeBalanceDialogOpen) {
		Dialog(onDismissRequest = { changeBalanceDialogOpen = false	}) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(16.dp),
				modifier = Modifier
					.background(MaterialTheme.colorScheme.background)
					.padding(16.dp)
			) {
				TextField(
					state = state.changeBalanceState,
					keyboardOptions = KeyboardOptions(
						keyboardType = KeyboardType.Decimal
					)
				)
				Text(
					text = stringResource(R.string.new_transaction_will_be_added)
				)
				Text(
					text = "${state.balanceDifference}",
					fontSize = 20.sp,
					fontWeight = FontWeight.Bold,
					color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
				)
				Button(
					onClick = {
						onAction(AccountAction.OnSaveChangeBalanceClick)
						changeBalanceDialogOpen = false
					},
					enabled = state.balanceDifference != 0.0,
					modifier = Modifier
						.align(Alignment.End)
				) {
					Text(
						text = stringResource(R.string.save)
					)
				}
			}
		}
	}

	if (deleteDialogOpen) {
		AlertDialog(
			onDismissRequest = { deleteDialogOpen = false },
			confirmButton = {
				Button(
					onClick = {
						onAction(AccountAction.OnDeleteClick)
						deleteDialogOpen = false
					},
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.error
					)
				) {
					Text(
						text = stringResource(R.string.delete_account)
					)
				}
			},
			dismissButton = {
				TextButton(
					onClick = { deleteDialogOpen = false }
				) {
					Text(
						text = stringResource(R.string.cancel)
					)
				}
			},
			text = {
				Text(
					text = stringResource(R.string.delete_account_confirmation)
				)
			}
		)
	}

	if (archiveDialogOpen) {
		Dialog(onDismissRequest = { archiveDialogOpen = false	}) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(16.dp),
				modifier = Modifier
					.background(MaterialTheme.colorScheme.background)
					.padding(16.dp)
			) {
				Text(
					text = if (state.account?.isArchived == true) {
						stringResource(R.string.archivation_return_confirmation)
					} else {
						stringResource(R.string.archivation_confirmation)
					}
				)
				Button(
					onClick = {
						onAction(AccountAction.OnArchiveClick)
						archiveDialogOpen = false
					},
					modifier = Modifier
						.align(Alignment.End)
				) {
					Text(
						text = if (state.account?.isArchived == true) {
							stringResource(R.string.return_from_archive)
						} else {
							stringResource(R.string.archive_account)
						}
					)
				}
			}
		}
	}

	Scaffold() { innerPadding ->
		if (state.isLoading) {
			CircularProgressIndicator(
				modifier = Modifier
					.fillMaxSize()
					.wrapContentSize()
			)
		} else {
			Column(
				verticalArrangement = Arrangement.spacedBy(16.dp),
				horizontalAlignment = Alignment.CenterHorizontally,
				modifier = Modifier
					.padding(innerPadding)
					.padding(16.dp)
					.fillMaxSize()
			) {
				Text(
					text = state.account?.name ?: "",
					fontSize = 24.sp
				)
				val formatter = remember { NumberFormat.getInstance() }
				formatter.maximumFractionDigits = 2
				formatter.minimumFractionDigits = 2
				val symbol = state.account?.currency?.symbol
				Text(
					text = "${formatter.format(state.account?.amount ?: 0.0)} $symbol",
					fontSize = 36.sp,
					modifier = Modifier
						.clickable {
							changeBalanceDialogOpen = true
						}
				)
				state.amountInDefaultCurrency?.let { amount ->
					state.defaultCurrency?.let { currency ->
						Text(
							text = "${formatter.format(amount)} ${currency.symbol}"
						)
					}
				}
				Row() {
					IconButton(
						onClick = {
							onAction(AccountAction.OnEditClick)
						}
					) {
						Icon(
							imageVector = Icons.Default.Edit,
							contentDescription = stringResource(R.string.edit_account)
						)
					}
					IconButton(
						onClick = {
							archiveDialogOpen = true
						},
						enabled = state.account?.isDefault != true
					) {
						Icon(
							imageVector = Icons.Default.Archive,
							contentDescription = stringResource(R.string.archive_account)
						)
					}
					IconButton(
						onClick = {
							deleteDialogOpen = true
						},
						enabled = state.account?.isDefault != true
					) {
						Icon(
							imageVector = Icons.Default.Delete,
							contentDescription = stringResource(R.string.delete_account)
						)
					}
				}
				LazyColumn(
					verticalArrangement = Arrangement.spacedBy(16.dp)
				) {
					val t = state.transactions.groupBy { it.timestamp.toLocalDate() }
					t.forEach { (date, transactions) ->
						stickyHeader {
							val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
							Text(
								text = formatter.format(date)
							)
						}
						items(
							items = transactions,
							key = { it.id }
						) { transaction ->
							TransactionItem(
								transaction = transaction,
								onClick = {
									onAction(AccountAction.OnTransactionClick(transaction.id))
								}
							)
						}
					}
				}
			}
		}
	}
}

@Composable
@Preview
private fun AccountPreview() {
	AccountRoot(
		state = AccountState(
			isLoading = false,
			account = Account(
				id = 0,
				currency = Currency.getInstance("USD"),
				initialAmount = 0.0,
				amount = 456.2524,
				name = "Cash"
			),
			amountInDefaultCurrency = 34042.95,
			defaultCurrency = Currency.getInstance("AMD")
		),
		onAction = {}
	)
}