package vadimerenkov.krona.core.presentation.onboarding

import androidx.compose.foundation.text.input.TextFieldState
import java.util.Currency
import java.util.Locale

data class OnboardingState(
	val currency: Currency = Currency.getInstance(Locale.getDefault()),
	val accountNameState: TextFieldState = TextFieldState()
) {
	val isValid
		get() = accountNameState.text.isNotBlank()
}