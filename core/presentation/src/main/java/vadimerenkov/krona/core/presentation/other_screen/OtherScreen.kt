package vadimerenkov.krona.core.presentation.other_screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import vadimerenkov.krona.core.presentation.BuildConfig
import vadimerenkov.krona.core.presentation.R
import java.time.LocalDateTime
import java.time.Year
import java.time.format.DateTimeFormatter

@Composable
fun OtherScreen(
	viewModel: OtherViewModel = koinViewModel(),
	onCategoriesClick: () -> Unit,
	onDebtsClick: () -> Unit,
	onImportClick: () -> Unit,
	onSettingsClick: () -> Unit
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
	) {
		OtherMenuButton(
			text = stringResource(R.string.categories),
			onClick = onCategoriesClick
		)
		OtherMenuButton(
			text = stringResource(R.string.debts),
			onClick = onDebtsClick
		)
		OtherMenuButton(
			text = stringResource(R.string.import_csv),
			onClick = onImportClick
		)

		val context = LocalContext.current
		val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
			if (uri != null) {
				viewModel.exportCsv(uri, context)
			}
		}

		OtherMenuButton(
			text = stringResource(R.string.export_csv),
			onClick = {
				val date = LocalDateTime.now()
				val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss")
				val dateString = formatter.format(date)
				exportLauncher.launch("krona-export-csv-$dateString")
			}
		)
		OtherMenuButton(
			text = stringResource(R.string.settings),
			onClick = onSettingsClick,
			icon = Icons.Default.Settings
		)
		Spacer(
			modifier = Modifier.weight(1f)
		)
		val currentYear = Year.now()
		val yearsString = if (currentYear.value == 2026) "2026" else "2026-${currentYear.value}"
		val version = BuildConfig.VERSION_NAME
		val handler = LocalUriHandler.current
		Text(
			text = "Krona\nVadim Erenkov © $yearsString\nv. $version",
			textAlign = TextAlign.Center
		)
		TextButton(
			onClick = {
				handler.openUri("https://github.com/vadimerenkov/Krona")
			}
		) {
			Icon(
				painter = painterResource(R.drawable.github),
				contentDescription = null,
				modifier = Modifier
					.size(24.dp)
			)
			Text(
				text = stringResource(R.string.source_code),
				style = MaterialTheme.typography.bodyLarge,
				textDecoration = TextDecoration.Underline,
				modifier = Modifier
					.padding(horizontal = 8.dp)
			)
			Icon(
				painter = painterResource(R.drawable.open_in_new),
				contentDescription = null
			)
		}
		TextButton(
			onClick = {
				handler.openUri("https://ko-fi.com/vadimerenkov")
			}
		) {
			Icon(
				painter = painterResource(R.drawable.kofi_symbol),
				contentDescription = null,
				tint = Color.Unspecified,
				modifier = Modifier
					.size(24.dp)
			)
			Text(
				text = stringResource(R.string.support),
				style = MaterialTheme.typography.bodyLarge,
				textDecoration = TextDecoration.Underline,
				modifier = Modifier
					.padding(horizontal = 8.dp)
			)
			Icon(
				painter = painterResource(R.drawable.open_in_new),
				contentDescription = null
			)
		}
	}
}

@Composable
private fun OtherMenuButton(
	text: String,
	onClick: () -> Unit,
	icon: ImageVector? = null
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier
			.fillMaxWidth()
			.clickable {
				onClick()
			}
			.padding(16.dp)
	) {
		if (icon != null) {
			Icon(
				imageVector = icon,
				contentDescription = null
			)
			Spacer(modifier = Modifier.width(8.dp))
		}
		Text(
			text = text,
			fontSize = 24.sp
		)
	}
}