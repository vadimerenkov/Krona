package vadimerenkov.krona.core.presentation.edit_transaction

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vadimerenkov.krona.core.domain.ExchangeRatesRepository
import vadimerenkov.krona.core.domain.Person
import vadimerenkov.krona.core.domain.Place
import vadimerenkov.krona.core.domain.Transaction
import vadimerenkov.krona.core.domain.TransactionType
import vadimerenkov.krona.core.domain.TransactionsRepository
import vadimerenkov.krona.core.domain.settings.Settings
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class EditTransactionViewModel(
	val id: Long,
	private val transactionsRepository: TransactionsRepository,
	private val exchangeRatesRepository: ExchangeRatesRepository,
	private val settings: Settings
): ViewModel() {

	var state by mutableStateOf(EditTransactionState())
	val formatter = NumberFormat.getInstance()

	private val channel = Channel<EditTransactionEvent>()
	val events = channel.receiveAsFlow()

	init {
		formatter.minimumFractionDigits = 2
		formatter.maximumFractionDigits = 2
		viewModelScope.launch {
			val defaultAccount = transactionsRepository.getDefaultAccount()
			val accounts = transactionsRepository.getAccounts().first().filter { !it.isArchived }
			val categories = transactionsRepository.getCategories().first()
			val persons = transactionsRepository.getPersons().first()
			val places = transactionsRepository.getPlaces()
			val transaction = if (id == 0L) null else transactionsRepository.getTransactionById(id)
			if (transaction != null) {
				state.amountState.edit {
					replace(0, length, transaction.amount.toString())
				}
				state.placeState.edit {
					replace(0, length, transaction.place ?: "")
				}
				state.noteState.edit {
					replace(0, length, transaction.note ?: "")
				}
			}
			val personAmounts = transaction?.payees
				?.zip(transaction.splitAmounts)
				?.map { payee ->
					PersonAmount(
						personState = TextFieldState(payee.first.name),
						amountState = TextFieldState(payee.second.toString())
					)
			} ?: emptyList()
			state = state.copy(
				account = transaction?.account ?: defaultAccount,
				allAccounts = accounts,
				allCategories = categories,
				allPersons = persons,
				allPlaces = places,
				category = transaction?.category,
				date = transaction?.timestamp?.toLocalDate() ?: LocalDate.now(),
				time = transaction?.timestamp?.toLocalTime() ?: LocalTime.now(),
				type = transaction?.type ?: TransactionType.INCOME_EXPENSE,
				personAmounts = personAmounts
			)
		}
	}

	fun onAction(action: EditTransactionAction) {
		when (action) {
			EditTransactionAction.OnSaveClick -> {
				viewModelScope.launch {
					saveTransactions()
					channel.send(EditTransactionEvent.SavingFinished)
				}
			}

			is EditTransactionAction.ChangeAccount -> {
				state = state.copy(account = action.account)
			}

			is EditTransactionAction.ChangeCategory -> {
				state = state.copy(category = action.category)
			}

			is EditTransactionAction.ChangeDate -> {
				state = state.copy(date = action.date)
			}
			is EditTransactionAction.ChangeTime -> {
				state = state.copy(time = action.time)
			}

			is EditTransactionAction.ChangeIncomingAccount -> {
				state = state.copy(incomingAccount = action.account)
			}
			is EditTransactionAction.ChangeOutgoingAccount -> {
				state = state.copy(outgoingAccount = action.account)
			}

			is EditTransactionAction.ChangeType -> {
				state = state.copy(type = action.type)
			}

			EditTransactionAction.AddPersonAmount -> {
				addPerson()
			}

			is EditTransactionAction.RemovePersonAmount -> {
				state = state.copy(personAmounts = state.personAmounts - action.personAmount)
			}
		}
	}

	fun addPerson() {
		val amount = if (state.amountState.text.toString().toDoubleOrNull() != null) {
			state.amountState.text.toString().toDouble() / (state.personAmounts.size + 1)
		} else 0.0
		state = state.copy(personAmounts = state.personAmounts.map { personAmount ->
			PersonAmount(
				personState = personAmount.personState,
				amountState = TextFieldState(initialText = formatter.format(amount))
			)
		} + PersonAmount(
			amountState = TextFieldState(initialText = formatter.format(amount))
		))
	}

	suspend fun saveTransactions() {
		when (state.type) {
			TransactionType.INCOME_EXPENSE -> {
				val defaultCurrency = settings.state.value.defaultCurrency
				val amounts = state.personAmounts
					.mapNotNull { personAmount ->
						personAmount.amountState.text.toString().toDoubleOrNull()
					}
				val payees = state.personAmounts
					.map { personAmount ->
						personAmount.personState.text.toString().trim()
					}
					.filterNot { it.isBlank() }
					.map { name ->
						transactionsRepository.getPersonByName(name) ?: Person(name = name, amountOwed = 0.0, lastUpdated = ZonedDateTime.now())
					}
					.zip(amounts) { person, amount ->
						val amountInDefaultCurrency = exchangeRatesRepository.getAmountInCurrency(amount, state.account!!.currency, defaultCurrency) ?: 0.0
						person.copy(amountOwed = person.amountOwed + amountInDefaultCurrency, lastUpdated = ZonedDateTime.now())
					}

				transactionsRepository.saveTransaction(Transaction(
					id = id,
					account = state.account!!,
					amount = state.amountState.text.toString().toDouble(),
					timestamp = state.date.atTime(state.time).atZone(ZoneId.systemDefault()),
					category = state.category,
					type = state.type,
					place = state.placeState.text.toString().ifBlank { null },
					note = state.noteState.text.toString().ifBlank { null },
					payees = payees,
					splitAmounts = amounts
				)
				)
				transactionsRepository.savePersons(payees)
				if (state.placeState.text.toString().isNotBlank()) {
					transactionsRepository.savePlace(Place(
						name = state.placeState.text.toString(),
						lastUpdated = ZonedDateTime.now()
					))
				}
			}
			TransactionType.TRANSFER -> {
				transactionsRepository.saveTransaction(Transaction(
					account = state.outgoingAccount!!,
					amount = -state.outgoingAmount.text.toString().toDouble(),
					timestamp = state.date.atTime(state.time).atZone(ZoneId.systemDefault()),
					type = state.type
				))
				transactionsRepository.saveTransaction(Transaction(
					account = state.incomingAccount!!,
					amount = state.incomingAmount.text.toString().toDouble(),
					timestamp = state.date.atTime(state.time).atZone(ZoneId.systemDefault()),
					type = state.type
				))
			}
		}
	}
}