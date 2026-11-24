package vadimerenkov.krona.core.presentation.categories_list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vadimerenkov.krona.core.domain.Category

@Composable
fun CategoriesList(
	categories: List<Category>,
	disabledCategories: List<Category>,
	onClick: (Category) -> Unit,
	modifier: Modifier = Modifier
) {
	LazyColumn(
		modifier = modifier
	) {
		categories.forEach { category ->
			node(
				category = category,
				depth = 0,
				onClick = onClick,
				disabledCategories = disabledCategories
			)
		}
	}
}

private fun LazyListScope.node(
	category: Category,
	depth: Int,
	onClick: (Category) -> Unit,
	disabledCategories: List<Category>
) {
	val isEnabled = category.id !in disabledCategories.map { it.id }
	item {
		Text(
			text = category.name,
			fontSize = 20.sp,
			color = if (isEnabled) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
			modifier = Modifier
				.background(Color(category.color))
				.clickable {
					if (isEnabled) {
						onClick(category)
					}
				}
				.padding(start = 32.dp * depth)
				.padding(8.dp)
				.fillMaxWidth()
		)
	}
	category.childCategories.forEach {
		node(it, depth + 1, onClick, disabledCategories)
	}
}