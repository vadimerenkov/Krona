package vadimerenkov.krona.core.presentation.debts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.koin.compose.viewmodel.koinViewModel
import vadimerenkov.krona.core.presentation.R
import java.text.NumberFormat

@Composable
fun DebtsScreen(
	viewModel: DebtsViewModel = koinViewModel()
) {
	DebtsScreenRoot(
		state = viewModel.state,
		onAction = viewModel::onAction
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DebtsScreenRoot(
	state: DebtsState,
	onAction: (DebtsAction) -> Unit
) {
	var changeDebtDialogOpen by remember { mutableStateOf(false) }

	if (changeDebtDialogOpen) {
		Dialog(
			onDismissRequest = { changeDebtDialogOpen = false }
		) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				modifier = Modifier
					.background(MaterialTheme.colorScheme.background)
					.padding(16.dp)
			) {
				Text(
					text = state.selectedPerson?.name ?: ""
				)
				TextField(
					state = state.selectedDebtState
				)
				Button(
					enabled = state.selectedDebtState.text.toString().replace(',','.').toDoubleOrNull() != null,
					onClick = {
						onAction(DebtsAction.SaveChangeClick)
						changeDebtDialogOpen = false
					}
				) {
					Text(
						text = stringResource(R.string.save)
					)
				}
			}
		}
	}

	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Text(
						text = stringResource(R.string.debts)
					)
				},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.primaryContainer
				)
			)
		}
	) { innerPadding ->
		if (state.persons.size == 1) {
			Text(
				text = stringResource(R.string.no_debts_yet),
				color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
				modifier = Modifier
					.fillMaxSize()
					.wrapContentSize()
			)
		} else {
			LazyColumn(
				contentPadding = PaddingValues(16.dp),
				modifier = Modifier
					.padding(innerPadding)
			) {
				items(
					items = state.persons,
					key = { it.name }
				) { person ->
					if (!person.isMe) {
						Row(
							horizontalArrangement = Arrangement.spacedBy(16.dp),
							verticalAlignment = Alignment.CenterVertically,
							modifier = Modifier
								.clickable {
									changeDebtDialogOpen = true
									onAction(DebtsAction.SelectPerson(person))
								}
								.padding(16.dp)
								.fillMaxWidth()
						) {
							val formatter = remember { NumberFormat.getInstance() }
							formatter.maximumFractionDigits = 2
							formatter.minimumFractionDigits = 2
							Text(
								text = person.name,
								fontSize = 18.sp
							)
							Text(
								text = formatter.format(person.amountOwed),
								fontWeight = FontWeight.Bold,
								color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
								fontSize = 18.sp
							)
						}
					}
				}
			}
		}
	}
}