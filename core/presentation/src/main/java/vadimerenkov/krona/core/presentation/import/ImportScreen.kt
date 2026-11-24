package vadimerenkov.krona.core.presentation.import

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import vadimerenkov.krona.core.presentation.R
import vadimerenkov.krona.core.presentation.util.ObserveAsEvents
import java.io.File
import java.nio.file.Files

@Composable
fun ImportScreen(
	viewModel: ImportViewModel = koinViewModel(),
	onBackClick: () -> Unit
) {
	ObserveAsEvents(viewModel.events) { event ->
		when (event) {
			ImportEvent.ImportFinished -> {
				onBackClick()
			}
		}
	}

	ImportRoot(
		state = viewModel.state,
		onAction = viewModel::onAction
	)
}

@Composable
private fun ImportRoot(
	state: ImportState,
	onAction: (ImportAction) -> Unit
) {
	val context = LocalContext.current
	val launcher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.OpenDocument()
	) { uri ->
		if (uri != null) {
			val file = File.createTempFile("csv_importer", ".csv")
			context.contentResolver.openInputStream(uri).use { stream ->
				file.delete()
				Files.copy(stream, file.toPath())
			}
			onAction(ImportAction.ImportFileChosen(file))
		}
	}
	Scaffold() { innerPadding ->
		Column(
			modifier = Modifier
				.padding(innerPadding)
		) {
			Text(
				text = stringResource(R.string.import_from),
				modifier = Modifier
					.padding(start = 16.dp)
			)
			ImportButton(
				icon = painterResource(R.drawable.zenmoney),
				text = stringResource(R.string.zenmoney),
				onClick = {
					launcher.launch(arrayOf("text/*"))
					onAction(ImportAction.OnZenMoneyClick)
				}
			)
			ImportButton(
				icon = painterResource(R.drawable.cashew),
				text = stringResource(R.string.cashew),
				onClick = {
					launcher.launch(arrayOf("text/*"))
					onAction(ImportAction.OnCashewClick)
				}
			)
		}
	}
}

@Composable
private fun ImportButton(
	icon: Painter,
	text: String,
	onClick: () -> Unit
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier
			.clickable {
				onClick()
			}
			.padding(16.dp)
			.fillMaxWidth()
	) {
		Image(
			painter = icon,
			contentDescription = null,
			modifier = Modifier
				.size(64.dp)
		)
		Text(
			text = text,
			fontSize = 18.sp
		)
	}
}