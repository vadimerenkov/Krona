package vadimerenkov.krona.core.presentation.onboarding

import android.content.Context
import java.util.Currency

sealed interface OnboardingAction {
	data class ChangeCurrency(val currency: Currency): OnboardingAction
	data class OnSaveClick(val context: Context): OnboardingAction
}