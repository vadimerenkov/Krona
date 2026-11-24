package vadimerenkov.krona.core.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import vadimerenkov.krona.core.domain.TransactionType

@Entity(
	foreignKeys = [
		ForeignKey(
			entity = AccountData::class,
			parentColumns = ["id"],
			childColumns = ["accountId"],
			onDelete = CASCADE
		)
	]
)
data class TransactionData(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val amount: Double,
	val timestamp: Long,
	val accountId: Long,
	val categoryId: Long?,
	val place: String?,
	val note: String?,
	val type: TransactionType,
	val payees: String,
	val splitAmounts: String
)
