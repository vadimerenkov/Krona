package vadimerenkov.krona.core.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import vadimerenkov.krona.core.data.entities.PersonData

@Dao
interface PersonDao {

	@Upsert
	suspend fun savePerson(person: PersonData)

	@Upsert
	suspend fun savePersons(persons: List<PersonData>)

	@Query("SELECT * FROM persondata ORDER BY lastUpdated")
	suspend fun getAllPersons(): List<PersonData>

	@Query("SELECT * FROM persondata WHERE name = :name")
	suspend fun getPersonByName(name: String): PersonData?

	@Query("SELECT * FROM persondata ORDER BY lastUpdated")
	fun getPersons(): Flow<List<PersonData>>
}