package vadimerenkov.krona.core.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.koin.compose.viewmodel.koinViewModel
import vadimerenkov.krona.core.presentation.R
import vadimerenkov.krona.core.presentation.util.ObserveAsEvents
import java.util.Currency

@Composable
fun OnboardingScreen(
	viewModel: OnboardingViewModel = koinViewModel(),
	goToMainScreen: () -> Unit
) {
	ObserveAsEvents(viewModel.events) { event ->
		when (event) {
			OnboardingEvent.SavingFinished -> goToMainScreen()
		}
	}

	OnboardingRoot(
		state = viewModel.state,
		onAction = viewModel::onAction
	)
}

@Composable
private fun OnboardingRoot(
	state: OnboardingState,
	onAction: (OnboardingAction) -> Unit
) {
	Scaffold() { innerPadding ->
		Column(
			verticalArrangement = Arrangement.spacedBy(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier
				.padding(innerPadding)
				.padding(16.dp)
				.fillMaxSize()
		) {
			Text(
				text = stringResource(R.string.welcome),
				fontSize = 36.sp
			)
			Text(
				text = stringResource(R.string.choose_default_currency),
				fontSize = 18.sp
			)

			var currencyDialogOpen by remember { mutableStateOf(false) }

			if (currencyDialogOpen) {
				Dialog(
					onDismissRequest = { currencyDialogOpen = false }
				) {
					LazyColumn(
						modifier = Modifier
							.background(MaterialTheme.colorScheme.background)
							.padding(16.dp)
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
										onAction(OnboardingAction.ChangeCurrency(currency))
										currencyDialogOpen = false
									}
							)
						}
					}
				}
			}

			OutlinedButton(
				onClick = { currencyDialogOpen = true }
			) {
				Text(
					text = "${state.currency.displayName} (${state.currency.currencyCode})"
				)
			}

			Text(
				text = stringResource(R.string.choose_default_name),
				fontSize = 18.sp
			)

			TextField(
				state = state.accountNameState,
				placeholder = {
					Text(
						text = stringResource(R.string.cash)
					)
				}
			)

			val context = LocalContext.current
			Button(
				onClick = {
					onAction(OnboardingAction.OnSaveClick(context))
				},
				enabled = state.isValid
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
private fun OnboardingPreview() {
	OnboardingRoot(
		state = OnboardingState(),
		onAction = {}
	)
}