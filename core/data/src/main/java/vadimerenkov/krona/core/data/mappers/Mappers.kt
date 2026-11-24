package vadimerenkov.krona.core.data.mappers

import kotlinx.serialization.json.Json
import vadimerenkov.krona.core.data.entities.AccountData
import vadimerenkov.krona.core.data.entities.AccountWithAmount
import vadimerenkov.krona.core.data.entities.CategoryData
import vadimerenkov.krona.core.data.entities.PersonData
import vadimerenkov.krona.core.data.entities.PlaceData
import vadimerenkov.krona.core.data.entities.TransactionData
import vadimerenkov.krona.core.domain.Account
import vadimerenkov.krona.core.domain.Category
import vadimerenkov.krona.core.domain.Person
import vadimerenkov.krona.core.domain.Place
import vadimerenkov.krona.core.domain.Transaction
import java.time.Instant
import java.time.ZoneId
import java.util.Currency


fun AccountData.toAccountWithAmount(amount: Double): AccountWithAmount {
	return AccountWithAmount(
		id = id,
		currency = Currency.getInstance(currencyCode),
		name = name,
		initialAmount = initialAmount,
		amount = amount,
		isDefault = isDefault,
		isArchived = isArchived
	)
}

fun AccountWithAmount.toAccount(): Account {
	return Account(
		id = id,
		currency = currency,
		name = name,
		initialAmount = initialAmount,
		amount = amount,
		isDefault = isDefault,
		isArchived = isArchived
	)
}

fun Account.toData(): AccountData {
	return AccountData(
		id = id,
		currencyCode = currency.currencyCode,
		name = name,
		initialAmount = initialAmount,
		isDefault = isDefault,
		isArchived = isArchived
	)
}

fun Transaction.toData(): TransactionData {
	return TransactionData(
		id = id,
		amount = amount,
		timestamp = timestamp.toEpochSecond(),
		accountId = account.id,
		categoryId = category?.id,
		place = place,
		note = note,
		type = type,
		payees = Json.encodeToString(payees.map { it.name }),
		splitAmounts = Json.encodeToString(splitAmounts)
	)
}

fun TransactionData.toTransaction(
	account: Account,
	category: Category?,
	payees: List<Person>
): Transaction {
	return Transaction(
		id = id,
		account = account,
		amount = amount,
		timestamp = Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault()),
		category = category,
		note = note,
		place = place,
		type = type,
		payees = payees,
		splitAmounts = Json.decodeFromString(splitAmounts)
	)
}

fun CategoryData.toCategory(parentCategory: Category? = null): Category {
	return Category(
		id = id,
		name = name,
		parentCategory = parentCategory,
		color = color
	)
}
fun Category.toData(): CategoryData {
	return CategoryData(
		id = id,
		name = name,
		parentCategoryId = parentCategory?.id,
		color = color
	)
}

fun PersonData.toPerson(): Person {
	return Person(
		name = name,
		amountOwed = amountOwed,
		lastUpdated = Instant.ofEpochSecond(lastUpdated).atZone(ZoneId.systemDefault()),
		isMe = isMe
	)
}

fun Person.toData(): PersonData {
	return PersonData(
		name = name,
		amountOwed = amountOwed,
		lastUpdated = lastUpdated.toEpochSecond(),
		isMe = isMe
	)
}

fun Place.toData(): PlaceData {
	return PlaceData(
		name = name,
		lastUpdated = lastUpdated.toEpochSecond()
	)
}

fun PlaceData.toPlace(): Place {
	return Place(
		name = name,
		lastUpdated = Instant.ofEpochSecond(lastUpdated).atZone(ZoneId.systemDefault()),
	)
}