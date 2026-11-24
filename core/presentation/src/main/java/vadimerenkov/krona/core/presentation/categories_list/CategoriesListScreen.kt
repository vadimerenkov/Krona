package vadimerenkov.krona.core.presentation.categories_list

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import vadimerenkov.krona.core.domain.Category
import vadimerenkov.krona.core.presentation.R
import kotlin.random.Random

@Composable
fun CategoriesListScreen(
	viewModel: CategoriesListViewModel = koinViewModel(),
	onAddCategoryClick: () -> Unit,
	onEditCategoryClick: (Category) -> Unit
) {
	CategoriesListRoot(
		state = viewModel.state,
		onAction = { action ->
			when (action) {
				is CategoriesListAction.OnAddCategoryClick -> {
					onAddCategoryClick()
				}
				is CategoriesListAction.OnCategoryClick -> {
					onEditCategoryClick(action.category)
				}
			}
			viewModel.onAction(action)
		}
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoriesListRoot(
	state: CategoriesListState,
	onAction: (CategoriesListAction) -> Unit
) {
	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Text(
						text = stringResource(R.string.categories)
					)
				},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.primaryContainer
				)
			)
		},
		floatingActionButton = {
			FloatingActionButton(
				onClick = {
					onAction(CategoriesListAction.OnAddCategoryClick)
				}
			) {
				Icon(
					imageVector = Icons.Default.Add,
					contentDescription = stringResource(R.string.new_category)
				)
			}
		}
	) { innerPadding ->
		CategoriesList(
			categories = state.categories,
			disabledCategories = emptyList(),
			onClick = { category ->
				onAction(CategoriesListAction.OnCategoryClick(category))
			},
			modifier = Modifier
				.padding(innerPadding)
		)

	}
}

@Composable
@Preview
private fun CategoriesListPreview() {
	CategoriesListRoot(
		state = CategoriesListState(
			categories = listOf(
				Category(
					id = 0,
					name = "Food",
					color = Random.nextInt(),
					childCategories = listOf(
						Category(
							id = 1,
							name = "Groceries",
							color = Random.nextInt(),
							childCategories = listOf(
								Category(
									id = 3,
									color = Random.nextInt(),
									name = "Milk"
								)
							)
						),
						Category(
							id = 2,
							color = Random.nextInt(),
							name = "Restaurant"
						)
					)
				),
				Category(
					id = 4,
					color = Random.nextInt(),
					name = "Transport"
				)
			)
		),
		onAction = {  }
	)
}