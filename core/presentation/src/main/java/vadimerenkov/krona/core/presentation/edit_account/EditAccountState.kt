package vadimerenkov.krona.core.presentation.edit_account

import androidx.compose.foundation.text.input.TextFieldState
import java.util.Currency

data class EditAccountState(
	val nameState: TextFieldState = TextFieldState(),
	val initialAmountState: TextFieldState = TextFieldState(),
	val currency: Currency = Currency.getInstance("USD"),
	val isDefault: Boolean = false,
	val isDefaultEnabled: Boolean = true
) {
	val isValid
		get() = nameState.text.isNotBlank() && initialAmountState.text.toString().ifBlank { "0.0" }.toDoubleOrNull() != null
}
