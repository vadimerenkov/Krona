package vadimerenkov.krona.core.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import vadimerenkov.krona.core.data.entities.PlaceData

@Dao
interface PlacesDao {

	@Upsert
	suspend fun savePlace(place: PlaceData)

	@Query("SELECT * FROM placedata ORDER BY lastUpdated DESC")
	suspend fun getPlaces(): List<PlaceData>
}