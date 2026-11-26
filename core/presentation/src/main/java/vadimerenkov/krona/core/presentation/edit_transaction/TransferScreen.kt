package vadimerenkov.krona.core.presentation.edit_transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import vadimerenkov.krona.core.presentation.R
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TransferScreen(
	state: EditTransactionState,
	onAction: (EditTransactionAction) -> Unit
) {
	val formatter = remember { NumberFormat.getInstance() }
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		var outcomeAccountExpanded by remember { mutableStateOf(false) }
		ExposedDropdownMenuBox(
			expanded = outcomeAccountExpanded,
			onExpandedChange = { outcomeAccountExpanded = it }
		) {
			val string = if (state.outgoingAccount == null) "" else {
				"${state.outgoingAccount.name} (${formatter.format(state.outgoingAccount.amount)} ${state.outgoingAccount.currency.symbol})"
			}
			TextField(
				value = string,
				onValueChange = {},
				readOnly = true,
				trailingIcon = {
					Icon(
						imageVector = Icons.Default.ArrowDropDown,
						contentDescription = null
					)
				},
				label = {
					Text(
						text = stringResource(R.string.outgoing_account)
					)
				},
				modifier = Modifier
					.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
					.fillMaxWidth()
			)
			ExposedDropdownMenu(
				expanded = outcomeAccountExpanded,
				onDismissRequest = { outcomeAccountExpanded = false }
			) {
				state.allAccounts.forEach { account ->
					DropdownMenuItem(
						text = {
							Row(
								verticalAlignment = Alignment.CenterVertically,
								horizontalArrangement = Arrangement.spacedBy(16.dp)
							) {
								Text(
									text = account.name
								)
								Text(
									text = "${formatter.format(account.amount)} ${account.currency.symbol}",
									fontWeight = FontWeight.Bold,
									color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
								)
							}
						},
						onClick = {
							onAction(EditTransactionAction.ChangeOutgoingAccount(account))
							outcomeAccountExpanded = false
						}
					)
				}
			}
		}

		Icon(
			imageVector = Icons.Default.ArrowDownward,
			contentDescription = null
		)

		var incomeAccountExpanded by remember { mutableStateOf(false) }
		ExposedDropdownMenuBox(
			expanded = incomeAccountExpanded,
			onExpandedChange = { incomeAccountExpanded = it }
		) {
			val string = if (state.incomingAccount == null) "" else {
				"${state.incomingAccount.name} (${formatter.format(state.incomingAccount.amount)} ${state.incomingAccount.currency.symbol})"
			}
			TextField(
				value = string,
				onValueChange = {},
				readOnly = true,
				trailingIcon = {
					Icon(
						imageVector = Icons.Default.ArrowDropDown,
						contentDescription = null
					)
				},
				label = {
					Text(
						text = stringResource(R.string.incoming_account)
					)
				},
				modifier = Modifier
					.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
					.fillMaxWidth()
			)
			ExposedDropdownMenu(
				expanded = incomeAccountExpanded,
				onDismissRequest = { incomeAccountExpanded = false }
			) {
				state.allAccounts.forEach { account ->
					DropdownMenuItem(
						text = {
							Row(
								verticalAlignment = Alignment.CenterVertically,
								horizontalArrangement = Arrangement.spacedBy(16.dp)
							) {
								Text(
									text = account.name
								)
								Text(
									text = "${formatter.format(account.amount)} ${account.currency.symbol}",
									fontWeight = FontWeight.Bold,
									color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
								)
							}
						},
						onClick = {
							onAction(EditTransactionAction.ChangeIncomingAccount(account))
							incomeAccountExpanded = false
						}
					)
				}
			}
		}

		HorizontalDivider()

		TextField(
			state = state.outgoingAmount,
			keyboardOptions = KeyboardOptions(
				keyboardType = KeyboardType.Decimal
			),
			prefix = {
				Text(
					text = "-"
				)
			},
			placeholder = {
				Text(
					text = "0.0"
				)
			},
			modifier = Modifier
				.fillMaxWidth()
		)

		Icon(
			imageVector = Icons.Default.ArrowDownward,
			contentDescription = null
		)

		TextField(
			state = state.incomingAmount,
			keyboardOptions = KeyboardOptions(
				keyboardType = KeyboardType.Decimal
			),
			placeholder = {
				Text(
					text = "0.0"
				)
			},
			modifier = Modifier
				.fillMaxWidth()
		)
	}
}
