package vadimerenkov.krona.core.presentation.transactions_list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vadimerenkov.krona.core.domain.Account
import vadimerenkov.krona.core.domain.Category
import vadimerenkov.krona.core.domain.Transaction
import vadimerenkov.krona.core.domain.TransactionType
import vadimerenkov.krona.core.presentation.R
import java.text.NumberFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Currency
import kotlin.random.Random

@Composable
fun TransactionItem(
	transaction: Transaction,
	onClick: () -> Unit,
	modifier: Modifier = Modifier
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween,
		modifier = modifier
			.border(
				width = 1.dp,
				color = MaterialTheme.colorScheme.primary,
				shape = RoundedCornerShape(8.dp)
			)
			.clip(RoundedCornerShape(8.dp))
			.clickable {
				onClick()
			}
			.background(
				brush = Brush.linearGradient(
					colors = listOf(
						Color(transaction.category?.color ?: MaterialTheme.colorScheme.background.toArgb()),
						MaterialTheme.colorScheme.background
					)
				)
			)
			.padding(16.dp)
			.fillMaxWidth()
	) {
		Column() {
			Text(
				text = transaction.account.name
			)
			Text(
				text = when (transaction.type) {
					TransactionType.INCOME_EXPENSE -> transaction.category?.name ?: stringResource(R.string.no_category)
					TransactionType.TRANSFER -> stringResource(R.string.transfer)
				},
				fontSize = 20.sp
			)
			transaction.place?.let { place ->
				Text(
					text = place,
				)
			}
		}
		Column(
			horizontalAlignment = Alignment.End
		) {
			val sign = transaction.account.currency.symbol
			val t = remember { NumberFormat.getInstance() }
			t.minimumFractionDigits = 2
			t.maximumFractionDigits = 2
			transaction.note?.let { note ->
				Text(
					text = note
				)
			}
			Text(
				text = "${t.format(transaction.amount)} $sign",
				fontSize = 20.sp
			)
			val formatter = remember { DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT) }
			Text(
				text = transaction.timestamp.format(formatter)
			)
		}
	}
}

@Composable
@Preview
private fun TransactionItemPreview() {
	TransactionItem(
		transaction = Transaction(
			account = Account(
				id = 0,
				currency = Currency.getInstance("EUR"),
				name = "Cash"
			),
			amount = -15.96,
			place = "Nikora",
			note = "Milk and eggs",
			timestamp = ZonedDateTime.now(),
			category = Category(
				id = 0,
				name = "Groceries",
				color = Random.nextInt(),
			),
		),
		onClick = {}
	)
}