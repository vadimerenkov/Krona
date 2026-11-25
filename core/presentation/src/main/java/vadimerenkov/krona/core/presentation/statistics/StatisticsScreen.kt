package vadimerenkov.krona.core.presentation.statistics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.TextComponent
import com.patrykandpatrick.vico.compose.pie.PieChart
import com.patrykandpatrick.vico.compose.pie.PieChartHost
import com.patrykandpatrick.vico.compose.pie.PieSize
import com.patrykandpatrick.vico.compose.pie.data.PieChartModelProducer
import com.patrykandpatrick.vico.compose.pie.data.PieValueFormatter
import com.patrykandpatrick.vico.compose.pie.data.pieModel
import com.patrykandpatrick.vico.compose.pie.rememberPieChart
import org.koin.compose.viewmodel.koinViewModel
import vadimerenkov.krona.core.presentation.R
import vadimerenkov.krona.core.presentation.util.palette

@Composable
fun StatisticsScreen(
	viewModel: StatisticsViewModel = koinViewModel(),
	onPieChartClick: () -> Unit
) {
	StatisticsRoot(
		state = viewModel.state,
		onAction = { action ->
			when (action) {
				StatisticsAction.OnPieChartClick -> onPieChartClick()
			}
		}
	)
}

@Composable
private fun StatisticsRoot(
	state: StatisticsState,
	onAction: (StatisticsAction) -> Unit
) {
	val modelProducer = remember { PieChartModelProducer() }
	val noCategory = stringResource(R.string.no_category)

	LaunchedEffect(state.statsForMonth) {
		modelProducer.runTransaction {
			pieModel {
				series(state.statsForMonth.values)
			}
		}
	}

	Column() {
		if (state.statsForMonth.isEmpty()) {
			Text(
				text = stringResource(R.string.no_statistics),
				color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
				modifier = Modifier
					.fillMaxSize()
					.wrapContentSize()
			)
		} else {
			PieChartHost(
				chart = rememberPieChart(
					sliceProvider = PieChart.SliceProvider.series(
						state.statsForMonth.keys.map { category ->
							val color = Color(category?.color ?: palette[0].toArgb())
							PieChart.Slice(
								fill = Fill(color),
								label = PieChart.SliceLabel.Outside(
									textComponent = TextComponent(
										textStyle = TextStyle(MaterialTheme.colorScheme.onBackground)
									),
									lineColor = MaterialTheme.colorScheme.onBackground
								)
							)
						}
					),
					innerSize = PieSize.Inner.fixed(64.dp),
					spacing = 4.dp,
					valueFormatter = PieValueFormatter { _, value, _ ->
						state.statsForMonth.filterValues { it.toFloat() == value }.keys.firstOrNull()?.name
							?: noCategory
					}
				),
				modelProducer = modelProducer,
				chartAreaHeight = 128.dp,
				modifier = Modifier
					.clickable {
						onAction(StatisticsAction.OnPieChartClick)
					}
			)
		}
		Box(
			modifier = Modifier
				.weight(1f)
		)
	}
}