package vadimerenkov.krona.core.domain

data class Category(
	val id: Long,
	val name: String,
	val color: Int,
	val parentCategory: Category? = null,
	val childCategories: List<Category> = emptyList()
)
