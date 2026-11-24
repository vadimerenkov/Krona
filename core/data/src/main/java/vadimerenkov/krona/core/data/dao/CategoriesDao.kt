package vadimerenkov.krona.core.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import vadimerenkov.krona.core.data.entities.CategoryData

@Dao
interface CategoriesDao {

	@Upsert
	suspend fun saveCategory(category: CategoryData)

	@Upsert
	suspend fun saveCategories(categories: List<CategoryData>)

	@Query("DELETE FROM categorydata WHERE id = :id")
	suspend fun deleteCategory(id: Long)

	@Transaction
	@Query("SELECT * FROM categorydata WHERE id = :id")
	suspend fun getCategoryById(id: Long?): CategoryData?

	@Query("SELECT * FROM categorydata")
	fun getCategories(): Flow<List<CategoryData>>

	@Query("SELECT * FROM categorydata WHERE name = :name")
	suspend fun getCategoryByName(name: String): CategoryData?

	@Query("UPDATE categorydata SET parentCategoryId = :newParentCategoryId WHERE parentCategoryId = :oldParentCategoryId")
	suspend fun updateParentCategoryIds(oldParentCategoryId: Long, newParentCategoryId: Long?)
}