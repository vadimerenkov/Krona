package vadimerenkov.krona.core.presentation.edit_transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import vadimerenkov.krona.core.domain.TransactionType
import vadimerenkov.krona.core.presentation.R
import vadimerenkov.krona.core.presentation.util.ObserveAsEvents
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun EditTransactionScreen(
	onBackClick: () -> Unit,
	viewModel: EditTransactionViewModel = koinViewModel()
) {
	ObserveAsEvents(viewModel.events) { event ->
		when (event) {
			EditTransactionEvent.SavingFinished -> onBackClick()
		}
	}

	EditTransactionRoot(
		state = viewModel.state,
		onAction = viewModel::onAction
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTransactionRoot(
	state: EditTransactionState,
	onAction: (EditTransactionAction) -> Unit
) {
	Scaffold() { innerPadding ->
		Column(
			verticalArrangement = Arrangement.spacedBy(16.dp),
			modifier = Modifier
				.padding(innerPadding)
				.padding(16.dp)
				.fillMaxSize()
				.verticalScroll(rememberScrollState())
		) {
			SingleChoiceSegmentedButtonRow(
				modifier = Modifier
					.fillMaxWidth()
			) {
				SegmentedButton(
					selected = state.type == TransactionType.INCOME_EXPENSE,
					onClick = {
						onAction(EditTransactionAction.ChangeType(TransactionType.INCOME_EXPENSE))
					},
					shape = SegmentedButtonDefaults.itemShape(0, 2)
				) {
					Text(
						text = stringResource(R.string.income_expense)
					)
				}
				SegmentedButton(
					selected = state.type == TransactionType.TRANSFER,
					onClick = {
						onAction(EditTransactionAction.ChangeType(TransactionType.TRANSFER))
					},
					shape = SegmentedButtonDefaults.itemShape(1, 2)
				) {
					Text(
						text = stringResource(R.string.transfer)
					)
				}
			}

			when (state.type) {
				TransactionType.INCOME_EXPENSE -> {
					IncomeExpenseScreen(
						state = state,
						onAction = onAction
					)
				}
				TransactionType.TRANSFER -> {
					TransferScreen(
						state = state,
						onAction = onAction
					)
				}
			}

			var dateDialogOpen by remember { mutableStateOf(false) }
			val dateState = rememberDatePickerState(
				initialSelectedDate = state.date
			)

			if (dateDialogOpen) {
				DatePickerDialog(
					onDismissRequest = { dateDialogOpen = false },
					confirmButton = {
						Button(
							onClick = {
								onAction(EditTransactionAction.ChangeDate(dateState.getSelectedDate()!!))
								dateDialogOpen = false
							}
						) {
							Text(
								text = stringResource(R.string.save)
							)
						}
					}
				) {
					DatePicker(
						state = dateState
					)
				}
			}
			
			var timeDialogOpen by remember { mutableStateOf(false) }
			val timeState = rememberTimePickerState(
				initialHour = state.time.hour,
				initialMinute = state.time.minute
			)

			if (timeDialogOpen) {
				TimePickerDialog(
					onDismissRequest = { timeDialogOpen = false },
					confirmButton = {
						Button(
							onClick = {
								onAction(EditTransactionAction.ChangeTime(
									LocalTime.of(timeState.hour, timeState.minute)
								))
								timeDialogOpen = false
							}
						) {
							Text(
								text = stringResource(R.string.save)
							)
						}
					},
					title = {
						Text(
							text = stringResource(R.string.choose_time)
						)
					}
				) {
					TimePicker(
						state = timeState
					)
				}
			}

			val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT) }
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(16.dp)
			) {
				Text(
					text = stringResource(R.string.date),
					fontSize = 18.sp
				)
				OutlinedButton(
					onClick = { dateDialogOpen = true }
				) {
					Text(
						text = state.date.format(dateFormatter)
					)
				}
			}

			val timeFormatter = remember { DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT) }
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(16.dp)
			) {
				Text(
					text = stringResource(R.string.time),
					fontSize = 18.sp
				)
				OutlinedButton(
					onClick = { timeDialogOpen = true }
				) {
					Text(
						text = state.time.format(timeFormatter)
					)
				}
			}

			Spacer(modifier = Modifier.weight(1f))
			Button(
				onClick = {
					onAction(EditTransactionAction.OnSaveClick)
				},
				enabled = state.isValid,
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

@Composable
@Preview
private fun EditTransactionPreview() {
	EditTransactionRoot(
		state = EditTransactionState(
		),
		onAction = {}
	)
}