package vadimerenkov.krona.core.presentation.edit_account

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vadimerenkov.krona.core.domain.Account
import vadimerenkov.krona.core.domain.TransactionsRepository

class EditAccountViewModel(
	val id: Long,
	val repository: TransactionsRepository
): ViewModel() {

	var state by mutableStateOf(EditAccountState())

	private val eventChannel = Channel<EditAccountEvent>()
	val events = eventChannel.receiveAsFlow()

	init {
		viewModelScope.launch {
			val account = if (id == 0L) null else repository.getAccountById(id)
			if (account != null) {
				val nameState = TextFieldState(account.name)
				val initialAmountState = TextFieldState(account.initialAmount.toString())
				val currency = account.currency
				val isDefault = account.isDefault
				state = state.copy(
					nameState = nameState,
					initialAmountState = initialAmountState,
					currency = currency,
					isDefault = isDefault,
					isDefaultEnabled = !isDefault
				)
			}
		}
	}

	fun onAction(action: EditAccountAction) {
		when (action) {
			is EditAccountAction.ChangeCurrency -> {
				state = state.copy(currency = action.currency)
			}

			EditAccountAction.OnSaveClick -> {
				viewModelScope.launch {
					if (state.isDefault) {
						val defaultAccount = repository.getDefaultAccount()
						repository.saveAccount(defaultAccount.copy(isDefault = false))
					}
					repository.saveAccount(Account(
						id = id,
						currency = state.currency,
						name = state.nameState.text.toString().trim(),
						initialAmount = state.initialAmountState.text.toString().toDouble(),
						isDefault = state.isDefault
					))
					eventChannel.send(EditAccountEvent.SavingFinished)
				}
			}

			is EditAccountAction.ChangeIsDefault -> {
				state = state.copy(isDefault = action.isDefault)
			}
		}
	}
}