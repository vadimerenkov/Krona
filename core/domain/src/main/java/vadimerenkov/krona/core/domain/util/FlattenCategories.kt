package vadimerenkov.krona.core.domain.util

import vadimerenkov.krona.core.domain.Category

fun List<Category>.flatten(): List<Category> {
	val list = mutableListOf<Category>()
	forEach { category ->
		list.add(category)
		val children = category.childCategories.flatten()
		list.addAll(children)
	}
	return list
}