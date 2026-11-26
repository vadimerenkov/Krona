package vadimerenkov.krona.core.presentation.piechart

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import vadimerenkov.krona.core.presentation.transactions_list.TransactionItem
import vadimerenkov.krona.core.presentation.util.palette
import java.text.NumberFormat
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun PieChartScreen(
	viewModel: PieChartViewModel = koinViewModel()
) {
	PieChartRoot(
		state = viewModel.state,
		onAction = viewModel::onAction
	)
}

@Composable
private fun PieChartRoot(
	state: PieChartState,
	onAction: (PieChartAction) -> Unit
) {
	val modelProducer = remember { PieChartModelProducer() }
	val noCategory = stringResource(R.string.no_category)
	val numberFormat = remember { NumberFormat.getInstance() }
	val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL) }
	numberFormat.minimumFractionDigits = 2
	numberFormat.maximumFractionDigits = 2

	LaunchedEffect(state.statsForMonth) {
		modelProducer.runTransaction {
			pieModel {
				series(state.statsForMonth.values)
			}
		}
	}

	BackHandler(state.categoriesBackStack.isNotEmpty()) {
		onAction(PieChartAction.OnBackClick)
	}

	Scaffold() { innerPadding ->
		if (state.isLoading) {
			CircularProgressIndicator(
				modifier = Modifier
					.fillMaxSize()
					.wrapContentSize()
			)
		} else {
			Column(
				verticalArrangement = Arrangement.spacedBy(16.dp),
				modifier = Modifier
					.padding(innerPadding)
			) {
				SingleChoiceSegmentedButtonRow(
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 16.dp)
				) {
					SegmentedButton(
						selected = state.type == StatType.EXPENSE,
						onClick = {
							onAction(PieChartAction.ChangeType(StatType.EXPENSE))
						},
						shape = SegmentedButtonDefaults.itemShape(0, 2)
					) {
						Text(
							text = stringResource(R.string.expense)
						)
					}
					SegmentedButton(
						selected = state.type == StatType.INCOME,
						onClick = {
							onAction(PieChartAction.ChangeType(StatType.INCOME))
						},
						shape = SegmentedButtonDefaults.itemShape(1, 2)
					) {
						Text(
							text = stringResource(R.string.income)
						)
					}
				}
				LazyRow(
					horizontalArrangement = Arrangement.spacedBy(8.dp),
					reverseLayout = true
				) {
					items(
						count = 200,
						key = { it }
					) { index ->
						val monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
						val currentMonth = YearMonth.now()
						val previousMonth = currentMonth.minusMonths(index.toLong())
						Text(
							text = monthFormatter.format(previousMonth),
							modifier = Modifier
								.border(
									width = 1.dp,
									color = MaterialTheme.colorScheme.primary,
									shape = CircleShape
								)
								.clip(CircleShape)
								.background(
									color = if (state.selectedMonth == previousMonth) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
								)
								.clickable {
									onAction(PieChartAction.SelectMonth(previousMonth))
								}
								.padding(8.dp)

						)
					}
				}
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
				)
				LazyColumn {
					val items = state.statsForMonth.keys.toList()
					items(
						items = items,
						key = { "category ${it?.id}" }
					) { category ->
						Row(
							horizontalArrangement = Arrangement.SpaceBetween,
							verticalAlignment = Alignment.CenterVertically,
							modifier = Modifier
								.clickable {
									onAction(PieChartAction.OnCategoryClick(category?.id))
								}
								.fillMaxWidth()
								.drawBehind {
									val max = state.statsForMonth.maxOf { it.value }
									val ratio =
										((state.statsForMonth[category] ?: 0.0) / max).toFloat()
									drawRect(
										color = Color(category?.color ?: palette[0].toArgb()),
										size = Size(this.size.width * ratio, this.size.height)
									)
								}
								.padding(16.dp)
						) {
							Text(
								text = category?.name ?: noCategory,
								fontSize = 18.sp
							)
							Text(
								text = numberFormat.format(state.statsForMonth[category]),
								fontSize = 18.sp
							)
						}
					}
					val grouped = state.transactions
						.sortedByDescending { it.timestamp }
						.groupBy { it.timestamp.toLocalDate() }
					grouped.forEach { (date, transactions) ->
						stickyHeader {
							Text(
								text = dateFormatter.format(date),
								modifier = Modifier
									.background(MaterialTheme.colorScheme.background)
									.fillMaxWidth()
									.padding(top = 16.dp)
									.padding(horizontal = 16.dp)
							)
						}
						items(
							items = transactions,
							key = { "transaction ${it.id}" }
						) { transaction ->
							TransactionItem(
								transaction = transaction,
								onClick = {},
								modifier = Modifier
									.padding(top = 16.dp)
									.padding(horizontal = 16.dp)
							)
						}
					}

				}
			}
		}
	}
}