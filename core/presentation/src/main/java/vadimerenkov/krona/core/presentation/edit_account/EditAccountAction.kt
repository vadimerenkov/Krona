package vadimerenkov.krona.core.presentation.edit_account

import java.util.Currency

sealed interface EditAccountAction {
	data class ChangeCurrency(val currency: Currency): EditAccountAction
	data object OnSaveClick: EditAccountAction
	data class ChangeIsDefault(val isDefault: Boolean): EditAccountAction
}