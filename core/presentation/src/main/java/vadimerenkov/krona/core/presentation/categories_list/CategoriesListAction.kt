package vadimerenkov.krona.core.presentation.categories_list

import vadimerenkov.krona.core.domain.Category

sealed interface CategoriesListAction {
	data class OnCategoryClick(val category: Category): CategoriesListAction
	data object OnAddCategoryClick: CategoriesListAction
}