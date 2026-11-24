package vadimerenkov.krona.core.presentation.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.columnModel
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import org.koin.compose.viewmodel.koinViewModel
import vadimerenkov.krona.core.domain.Account
import vadimerenkov.krona.core.domain.Category
import vadimerenkov.krona.core.domain.Transaction
import vadimerenkov.krona.core.domain.TransactionType
import vadimerenkov.krona.core.presentation.R
import vadimerenkov.krona.core.presentation.util.ObserveAsEvents
import java.text.NumberFormat
import java.time.Month
import java.time.YearMonth
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Currency
import java.util.Locale
import kotlin.random.Random

private val bottomAxisFormatter = CartesianValueFormatter { _, x, _ ->
	Month.of(x.toInt()).getDisplayName(TextStyle.SHORT, Locale.getDefault())
}

@Composable
fun TransactionScreen(
	viewModel: TransactionViewModel = koinViewModel(),
	onBackClick: () -> Unit,
	onEditClick: (Long?) -> Unit
) {
	ObserveAsEvents(viewModel.events) { event ->
		when (event) {
			TransactionEvent.SavingFinished -> onBackClick()
		}
	}

	TransactionRoot(
		state = viewModel.state,
		onAction = { action ->
			when (action) {
				TransactionAction.OnEditClick -> onEditClick(viewModel.state.transaction?.id)
				else -> Unit
			}
			viewModel.onAction(action)
		}
	)
}

@Composable
private fun TransactionRoot(
	state: TransactionState,
	onAction: (TransactionAction) -> Unit
) {
	val formatter = remember { NumberFormat.getInstance() }
	formatter.minimumFractionDigits = 2
	formatter.maximumFractionDigits = 2
	val dateFormatter = remember { DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM) }
	val modelProducer = remember { CartesianChartModelProducer() }

	var deleteDialogOpen by remember { mutableStateOf(false) }

	LaunchedEffect(state.statsFor6Months) {
		val currentMonth = YearMonth.now()
		val months = (0 until 6).map { i ->
			val month = currentMonth.minusMonths(i.toLong())
			month
		}.reversed()

		modelProducer.runTransaction {
			columnModel {
				series(x = months.map { it.monthValue }, y = state.statsFor6Months)
			}
		}
	}

	if (deleteDialogOpen) {
		Dialog(
			onDismissRequest = { deleteDialogOpen = false }
		) {
			Column(
				horizontalAlignment = Alignment.End,
				modifier = Modifier
					.background(MaterialTheme.colorScheme.background)
					.padding(16.dp)
			) {
				Text(
					text = stringResource(R.string.delete_transaction_confirmation)
				)
				Button(
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.error
					),
					onClick = {
						onAction(TransactionAction.DeleteConfirmed)
						deleteDialogOpen = false
					}
				) {
					Text(
						text = stringResource(R.string.delete_transaction)
					)
				}
			}
		}
	}

	Scaffold() { innerPadding ->
		Column(
			verticalArrangement = Arrangement.spacedBy(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier
				.padding(innerPadding)
				.padding(16.dp)
				.fillMaxSize()
		) {
			state.transaction?.let { transaction ->
				Text(
					text = when (transaction.type) {
						TransactionType.INCOME_EXPENSE -> transaction.category?.name ?: stringResource(R.string.no_category)
						TransactionType.TRANSFER -> stringResource(R.string.transfer)
					},
					fontSize = 24.sp
				)
				Text(
					text = "${formatter.format(transaction.amount)} ${transaction.account.currency.symbol}",
					fontSize = 48.sp
				)
				Text(
					text = transaction.account.name,
					fontSize = 24.sp
				)
				transaction.place?.let { place ->
					Text(
						text = place
					)
				}
				Text(
					text = dateFormatter.format(transaction.timestamp)
				)
				Row {
					IconButton(
						onClick = {
							onAction(TransactionAction.OnEditClick)
						}
					) {
						Icon(
							imageVector = Icons.Default.Edit,
							contentDescription = stringResource(R.string.edit_transaction)
						)
					}
					IconButton(
						onClick = {
							deleteDialogOpen = true
						}
					) {
						Icon(
							imageVector = Icons.Default.Delete,
							contentDescription = stringResource(R.string.delete_transaction)
						)
					}
				}

				Column(
					modifier = Modifier
						.fillMaxWidth()
				) {
					transaction.payees
						.zip(transaction.splitAmounts)
						.forEach { (payee, amount) ->
							Row(
								horizontalArrangement = Arrangement.spacedBy(16.dp),
								verticalAlignment = Alignment.CenterVertically
							) {
								Text(
									text = payee.name
								)
								Text(
									text = formatter.format(amount),
									fontWeight = FontWeight.Bold,
									color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
								)
							}
						}
				}

				if (transaction.type == TransactionType.INCOME_EXPENSE) {
					CartesianChartHost(
						chart = rememberCartesianChart(
							rememberColumnCartesianLayer(),
							startAxis = VerticalAxis.rememberStart(),
							bottomAxis = HorizontalAxis.rememberBottom(
								valueFormatter = bottomAxisFormatter
							)
						),
						modelProducer
					)
				}
			}
		}
	}
}

@Composable
@Preview
private fun TransactionPreview() {
	TransactionRoot(
		state = TransactionState(
			transaction = Transaction(
				account = Account(
					id = 0,
					currency = Currency.getInstance("USD"),
					name = "Cash"
				),
				place = "Nikora",
				amount = -52.99,
				timestamp = ZonedDateTime.now(),
				category = Category(
					id = 0,
					name = "Groceries",
					color = Random.nextInt(),
				)
			)
		),
		onAction = {}
	)
}