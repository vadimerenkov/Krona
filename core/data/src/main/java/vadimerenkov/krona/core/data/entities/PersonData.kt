package vadimerenkov.krona.core.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PersonData(
	@PrimaryKey	val name: String,
	val amountOwed: Double,
	val lastUpdated: Long,
	val isMe: Boolean
)
