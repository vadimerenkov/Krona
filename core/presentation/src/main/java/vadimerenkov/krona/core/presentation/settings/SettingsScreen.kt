package vadimerenkov.krona.core.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import vadimerenkov.krona.core.domain.settings.Language
import vadimerenkov.krona.core.domain.settings.SettingsState
import vadimerenkov.krona.core.domain.settings.Theme
import vadimerenkov.krona.core.presentation.R
import vadimerenkov.krona.core.presentation.settings.util.DropdownSetting
import vadimerenkov.krona.core.presentation.settings.util.stringRes
import vadimerenkov.krona.core.presentation.theme.KronaTheme
import java.util.Currency

@Composable
fun SettingsScreen(
	viewModel: SettingsViewModel = koinViewModel()
) {
	val state by viewModel.settings.state.collectAsStateWithLifecycle()

	SettingsRoot(
		state = state,
		onAction = viewModel::onAction
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsRoot(
	state: SettingsState,
	onAction: (SettingsAction) -> Unit
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
								onAction(SettingsAction.ChangeDefaultCurrency(currency))
								currencyDialogOpen = false
							}
							.padding(16.dp)
					)
					HorizontalDivider()
				}
			}
		}
	}

	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Text(
						text = stringResource(R.string.settings)
					)
				},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.primaryContainer
				)
			)
		}
	) { innerPadding ->
		Column(
			modifier = Modifier
				.padding(innerPadding)
				.padding(16.dp)
		) {
			Row(
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.fillMaxWidth()
			) {
				Text(
					text = stringResource(R.string.default_currency)
				)
				OutlinedButton(
					onClick = {
						currencyDialogOpen = true
					}
				) {
					Text(
						text = state.defaultCurrency.currencyCode
					)
				}
			}
			val languages = Language.entries.map { it.endonym }.sorted()
			DropdownSetting(
				options = languages,
				description = stringResource(R.string.language),
				chosenOption = state.language?.endonym ?: "English",
				onOptionChosen = { index ->
					val language = Language.entries.first { it.endonym == languages[index] }
					onAction(SettingsAction.ChangeLanguage(language))
				},
				icon = Icons.Default.Language
			)
			DropdownSetting(
				options = Theme.entries.map { stringResource(it.stringRes) },
				description = stringResource(R.string.theme),
				chosenOption = stringResource(state.theme.stringRes),
				onOptionChosen = {
					onAction(SettingsAction.ChangeTheme(Theme.entries[it]))
				},
				icon = Icons.Default.Brightness4
			)
		}
	}
}

@Composable
@Preview
private fun SettingsPreview() {
	KronaTheme {
		SettingsRoot(
			state = SettingsState(),
			onAction = {}
		)
	}
}