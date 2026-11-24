package vadimerenkov.krona.core.presentation.edit_transaction

import androidx.compose.foundation.text.input.TextFieldState

data class PersonAmount(
	val personState: TextFieldState = TextFieldState(),
	val amountState: TextFieldState = TextFieldState()
)
