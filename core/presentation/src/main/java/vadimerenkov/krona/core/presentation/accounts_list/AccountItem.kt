package vadimerenkov.krona.core.presentation.accounts_list

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vadimerenkov.krona.core.domain.Account
import java.text.NumberFormat
import java.util.Currency

@Composable
fun AccountItem(
	account: Account,
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
			.clickable {
				onClick()
			}
			.padding(16.dp)
			.fillMaxWidth()
	) {
		Text(
			text = account.name,
			fontSize = 24.sp
		)
		val symbol = account.currency.symbol
		val t = remember { NumberFormat.getInstance() }
		t.minimumFractionDigits = 2
		t.maximumFractionDigits = 2
		Text(
			text = "${t.format(account.amount)} $symbol",
			fontSize = 24.sp
		)
	}
}

@Composable
@Preview
fun AccountItemPreview() {
	AccountItem(
		account = Account(
			id = 0,
			currency = Currency.getInstance("USD"),
			name = "Cash",
			amount = 306.14989418
		),
		onClick = {}
	)
}