package vadimerenkov.krona.core.presentation.onboarding

sealed interface OnboardingEvent {
	data object SavingFinished: OnboardingEvent
}