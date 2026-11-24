package vadimerenkov.krona.core.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlaceData(
	@PrimaryKey val name: String,
	val lastUpdated: Long
)
