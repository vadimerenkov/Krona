package vadimerenkov.krona.core.presentation.categories_list

import vadimerenkov.krona.core.domain.Category

data class CategoriesListState(
	val categories: List<Category> = emptyList()
)
