package vadimerenkov.krona.core.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CategoryData(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val name: String,
	val color: Int,
	val parentCategoryId: Long?
)
