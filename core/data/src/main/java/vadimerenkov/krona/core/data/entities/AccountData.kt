package vadimerenkov.krona.core.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccountData(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val currencyCode: String,
	val name: String,
	val initialAmount: Double,
	val isDefault: Boolean,
	val isArchived: Boolean
)
