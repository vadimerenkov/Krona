package vadimerenkov.krona.core.presentation.edit_account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.koin.compose.viewmodel.koinViewModel
import vadimerenkov.krona.core.presentation.R
import vadimerenkov.krona.core.presentation.util.ObserveAsEvents
import java.util.Currency

@Composable
fun EditAccountScreen(
	onBackClick: () -> Unit,
	viewModel: EditAccountViewModel = koinViewModel(),
) {
	ObserveAsEvents(viewModel.events) { event ->
		when (event) {
			EditAccountEvent.SavingFinished -> onBackClick()
		}
	}

	EditAccountRoot(
		state = viewModel.state,
		onAction = viewModel::onAction
	)
}

@Composable
private fun EditAccountRoot(
	state: EditAccountState,
	onAction: (EditAccountAction) -> Unit
) {
	var currencyDialogOpen by remember { mutableStateOf(false) }

	if (currencyDialogOpen) {
		Dialog(
			onDismissRequest = { currencyDialogOpen = false }
		) {
			LazyColumn(
				contentPadding = PaddingValues(16.dp),
				modifier = Modifier
					.background(MaterialTheme.colorScheme.background)
					.fillMaxSize()
			) {
				items(
					items = Currency.getAvailableCurrencies().toList().sortedBy { it.displayName },
					key = { it.currencyCode }
				) { currency ->
					Text(
						text = "${currency.displayName} (${currency.currencyCode})",
						modifier = Modifier
							.clickable {
								onAction(EditAccountAction.ChangeCurrency(currency))
								currencyDialogOpen = false
							}
							.padding(16.dp)
					)
					HorizontalDivider()
				}
			}
		}
	}

	Scaffold() { innerPadding ->
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
				.padding(16.dp)
		) {
			Column(
				verticalArrangement = Arrangement.spacedBy(16.dp)
			) {
				Row(
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier
						.fillMaxWidth()
				) {
					TextField(
						label = {
							Text(
								text = stringResource(R.string.account_name)
							)
						},
						state = state.nameState,
						modifier = Modifier
							.weight(1f)
					)
					Spacer(modifier = Modifier.width(8.dp))
					OutlinedButton(
						onClick = {
							currencyDialogOpen = true
						}
					) {
						Text(
							text = state.currency.currencyCode
						)
					}
				}
				TextField(
					state = state.initialAmountState,
					label = {
						Text(
							text = stringResource(R.string.initial_amount)
						)
					},
					placeholder = {
						Text(
							text = "0.0"
						)
					},
					keyboardOptions = KeyboardOptions(
						keyboardType = KeyboardType.Decimal
					),
					modifier = Modifier
						.fillMaxWidth()
				)
				Row(
					verticalAlignment = Alignment.CenterVertically
				) {
					Text(
						text = stringResource(R.string.set_as_default),
						fontSize = 18.sp
					)
					Checkbox(
						checked = state.isDefault,
						enabled = state.isDefaultEnabled,
						onCheckedChange = {
							onAction(EditAccountAction.ChangeIsDefault(it))
						}
					)
				}
			}
			Button(
				onClick = {
					onAction(EditAccountAction.OnSaveClick)
				},
				enabled = state.isValid,
				modifier = Modifier
					.align(Alignment.BottomEnd)
			) {
				Text(
					text = stringResource(R.string.save)
				)
			}
		}
	}
}

@Composable
@Preview
private fun EditAccountPreview() {
	EditAccountRoot(
		state = EditAccountState(),
		onAction = {}
	)
}