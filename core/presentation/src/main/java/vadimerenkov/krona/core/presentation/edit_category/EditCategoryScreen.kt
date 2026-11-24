package vadimerenkov.krona.core.presentation.edit_category

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.koin.compose.viewmodel.koinViewModel
import vadimerenkov.krona.core.presentation.R
import vadimerenkov.krona.core.presentation.categories_list.CategoriesList
import vadimerenkov.krona.core.presentation.util.ObserveAsEvents
import vadimerenkov.krona.core.presentation.util.palette

@Composable
fun EditCategoryScreen(
	viewModel: EditCategoryViewModel = koinViewModel(),
	onBackClick: () -> Unit
) {
	ObserveAsEvents(viewModel.events) { event ->
		when (event) {
			EditCategoryEvent.FinishedSaving -> onBackClick()
		}
	}
	EditCategoryRoot(
		state = viewModel.state,
		onAction = viewModel::onAction
	)
}

@Composable
private fun EditCategoryRoot(
	state: EditCategoryState,
	onAction: (EditCategoryAction) -> Unit
) {
	var categoriesDialogOpen by remember { mutableStateOf(false) }
	var deleteDialogOpen by remember { mutableStateOf(false) }
	var deleteCategoriesOpen by remember { mutableStateOf(false) }
	var colorDialogOpen by remember { mutableStateOf(false) }

	if (categoriesDialogOpen) {
		Dialog(
			onDismissRequest = { categoriesDialogOpen = false }
		) {
			CategoriesList(
				categories = state.allCategories,
				disabledCategories = state.disabledCategories,
				onClick = { category ->
					onAction(EditCategoryAction.ChangeParentCategory(category))
					categoriesDialogOpen = false
				},
				modifier = Modifier
					.background(MaterialTheme.colorScheme.background)
					.padding(16.dp)
			)
		}
	}

	if (deleteDialogOpen) {
		Dialog(
			onDismissRequest = { deleteDialogOpen = false }
		) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				modifier = Modifier
					.background(MaterialTheme.colorScheme.background)
					.padding(16.dp)
			) {
				Text(
					text = stringResource(R.string.delete_category_confirmation)
				)
				OutlinedButton(
					onClick = {
						deleteCategoriesOpen = true
					}
				) {
					Text(
						text = state.onDeleteCategory?.name ?: stringResource(R.string.no_category)
					)
				}
				Button(
					onClick = {
						onAction(EditCategoryAction.DeleteConfirmed)
					},
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.error
					)
				) {
					Text(
						text = stringResource(R.string.delete_category)
					)
				}
			}
		}
	}

	if (deleteCategoriesOpen) {
		Dialog(
			onDismissRequest = { deleteCategoriesOpen = false }
		) {
			CategoriesList(
				categories = state.allCategories,
				disabledCategories = emptyList(),
				onClick = { category ->
					onAction(EditCategoryAction.ChangeOnDeleteCategory(category))
					deleteCategoriesOpen = false
				},
				modifier = Modifier
					.background(MaterialTheme.colorScheme.background)
					.padding(16.dp)
			)
		}
	}

	if (colorDialogOpen) {
		Dialog(
			onDismissRequest = { colorDialogOpen = false }
		) {
			FlowRow(
				horizontalArrangement = Arrangement.spacedBy(4.dp),
				verticalArrangement = Arrangement.spacedBy(4.dp),
				maxItemsInEachRow = 8,
				modifier = Modifier
					.background(MaterialTheme.colorScheme.background)
					.padding(16.dp)
			) {
				palette.forEach { color ->
					Box(
						modifier = Modifier
							.clip(RoundedCornerShape(8.dp))
							.clickable {
								onAction(EditCategoryAction.ChangeColor(color))
								colorDialogOpen = false
							}
							.background(color)
							.size(36.dp)
					)
				}
			}
		}
	}

	Scaffold() { innerPadding ->
		Column(
			verticalArrangement = Arrangement.spacedBy(16.dp),
			modifier = Modifier
				.padding(innerPadding)
				.padding(16.dp)
				.fillMaxSize()
		) {
			TextField(
				state = state.nameState,
				label = {
					Text(
						text = stringResource(R.string.category_name)
					)
				}
			)
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {
				Text(
					text = stringResource(R.string.parent_category),
					fontSize = 18.sp
				)
				OutlinedButton(
					onClick = { categoriesDialogOpen = true }
				) {
					Text(
						text = state.parentCategory?.name ?: stringResource(R.string.no_category)
					)
				}
			}
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {
				Text(
					text = stringResource(R.string.color),
					fontSize = 18.sp
				)
				Box(
					modifier = Modifier
						.clip(RoundedCornerShape(8.dp))
						.clickable {
							colorDialogOpen = true
						}
						.border(
							width = 1.dp,
							color = MaterialTheme.colorScheme.primary,
							shape = RoundedCornerShape(8.dp)
						)
						.background(state.color)
						.size(36.dp)
				)
				if (state.color != Color.Transparent) {
					IconButton(
						onClick = {
							onAction(EditCategoryAction.ChangeColor(Color.Transparent))
						}
					) {
						Icon(
							imageVector = Icons.Default.Close,
							contentDescription = stringResource(R.string.clear_color)
						)
					}
				}
			}
			Spacer(modifier = Modifier.weight(1f))
			Row(
				horizontalArrangement = Arrangement.SpaceBetween,
				modifier = Modifier
					.fillMaxWidth()
			) {
				Button(
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.error
					),
					enabled = state.isDeleteEnabled,
					onClick = {
						deleteDialogOpen = true
					}
				) {
					Text(
						text = stringResource(R.string.delete_category)
					)
				}
				Button(
					onClick = {
						onAction(EditCategoryAction.OnSaveClick)
					},
					enabled = state.isValid
				) {
					Text(
						text = stringResource(R.string.save)
					)
				}
			}
		}
	}
}

@Composable
@Preview
private fun EditCategoryPreview() {
	EditCategoryRoot(
		state = EditCategoryState(),
		onAction = {}
	)
}