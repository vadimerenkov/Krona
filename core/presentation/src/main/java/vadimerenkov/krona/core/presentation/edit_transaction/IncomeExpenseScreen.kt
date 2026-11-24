package vadimerenkov.krona.core.presentation.edit_transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import vadimerenkov.krona.core.presentation.R
import vadimerenkov.krona.core.presentation.categories_list.CategoriesList
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeExpenseScreen(
	state: EditTransactionState,
	onAction: (EditTransactionAction) -> Unit
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		val formatter = remember { NumberFormat.getInstance() }
		formatter.maximumFractionDigits = 2
		TextField(
			state = state.amountState,
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
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(16.dp)
		) {
			Text(
				text = stringResource(R.string.account),
				fontSize = 18.sp
			)
			var expanded by remember { mutableStateOf(false) }
			ExposedDropdownMenuBox(
				expanded = expanded,
				onExpandedChange = { expanded = it }
			) {
				TextField(
					value = if (state.account != null) {
						"${state.account.name} (${formatter.format(state.account.amount)} ${state.account.currency.symbol})"
					} else "",
					onValueChange = {},
					readOnly = true,
					trailingIcon = {
						Icon(
							imageVector = Icons.Default.ArrowDropDown,
							contentDescription = null
						)
					},
					modifier = Modifier
						.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
				)
				ExposedDropdownMenu(
					expanded = expanded,
					onDismissRequest = { expanded = false }
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
								onAction(EditTransactionAction.ChangeAccount(account))
								expanded = false
							}
						)
					}
				}
			}
		}

		var categoryDialogOpen by remember { mutableStateOf(false) }

		if (categoryDialogOpen) {
			Dialog(
				onDismissRequest = { categoryDialogOpen = false }
			) {
				Box(
					modifier = Modifier
						.background(MaterialTheme.colorScheme.background)
						.padding(16.dp)
				) {
					CategoriesList(
						categories = state.allCategories,
						disabledCategories = emptyList(),
						onClick = { category ->
							onAction(EditTransactionAction.ChangeCategory(category))
							categoryDialogOpen = false
						}
					)
				}
			}
		}

		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(16.dp)
		) {
			Text(
				text = stringResource(R.string.category),
				fontSize = 18.sp
			)
			OutlinedButton(
				onClick = { categoryDialogOpen = true }
			) {
				Text(
					text = state.category?.name ?: stringResource(R.string.no_category)
				)
			}
		}

		if (state.personAmounts.isNotEmpty()) {
			Text(
				text = formatter.format(state.splitDifference),
				fontWeight = FontWeight.Bold,
				color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
				modifier = Modifier
					.align(Alignment.End)
			)
		}

		state.personAmounts.forEach { personAmount ->
			PersonAmountItem(
				state = state,
				personAmount = personAmount,
				onDeletePersonAmountClick = {
					onAction(EditTransactionAction.RemovePersonAmount(personAmount))
				}
			)
		}

		if (state.personAmounts.isNotEmpty()) {
			Text(
				text = formatter.format(state.totalSplitAmount),
				fontWeight = FontWeight.Bold,
				color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
				modifier = Modifier
					.align(Alignment.End)
			)
		}

		OutlinedButton(
			onClick = {
				onAction(EditTransactionAction.AddPersonAmount)
			},
			modifier = Modifier
				.fillMaxWidth()
		) {
			Icon(
				imageVector = Icons.Default.Add,
				contentDescription = null
			)
			Text(
				text = stringResource(R.string.add_person)
			)
		}

		var expanded by remember { mutableStateOf(false) }
		Row(
			horizontalArrangement = Arrangement.spacedBy(16.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = stringResource(R.string.place),
				fontSize = 18.sp
			)
			ExposedDropdownMenuBox(
				expanded = expanded,
				onExpandedChange = { expanded = it }
			) {
				TextField(
					state = state.placeState,
					trailingIcon = {
						Icon(
							imageVector = Icons.Default.ArrowDropDown,
							contentDescription = null
						)
					},
					modifier = Modifier
						.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
				)
				ExposedDropdownMenu(
					expanded = expanded,
					onDismissRequest = { expanded = false }
				) {
					state.allPlaces.forEach { place ->
						DropdownMenuItem(
							text = {
								Text(
									text = place.name
								)
							},
							onClick = {
								state.placeState.edit {
									replace(0, length, place.name)
								}
								expanded = false
							}
						)
					}
				}
			}
		}

		Row(
			horizontalArrangement = Arrangement.spacedBy(16.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = stringResource(R.string.note),
				fontSize = 18.sp
			)
			TextField(
				state = state.noteState
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PersonAmountItem(
	state: EditTransactionState,
	personAmount: PersonAmount,
	onDeletePersonAmountClick: () -> Unit
) {
	Row(
		verticalAlignment = Alignment.CenterVertically
	) {
		IconButton(
			onClick = {
				onDeletePersonAmountClick()
			}
		) {
			Icon(
				imageVector = Icons.Default.Close,
				contentDescription = stringResource(R.string.delete_person)
			)
		}
		var expanded by remember { mutableStateOf(false) }
		ExposedDropdownMenuBox(
			expanded = expanded,
			onExpandedChange = { expanded = it }
		) {
			OutlinedTextField(
				state = personAmount.personState,
				trailingIcon = {
					Icon(
						imageVector = Icons.Default.ArrowDropDown,
						contentDescription = null
					)
				},
				modifier = Modifier
					.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
					.widthIn(max = 200.dp)
			)
			ExposedDropdownMenu(
				expanded = expanded,
				onDismissRequest = { expanded = false }
			) {
				state.allPersons.forEach { person ->
					DropdownMenuItem(
						text = {
							Text(
								text = person.name
							)
						},
						onClick = {
							personAmount.personState.edit {
								replace(0, length, person.name)
							}
							expanded = false
						}
					)
				}
			}
		}
		TextField(
			state = personAmount.amountState,
			keyboardOptions = KeyboardOptions(
				keyboardType = KeyboardType.Decimal
			)
		)
	}
}