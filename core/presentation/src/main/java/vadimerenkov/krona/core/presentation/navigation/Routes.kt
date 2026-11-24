package vadimerenkov.krona.core.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object MainPager: NavKey

@Serializable
data class EditTransaction(val id: Long): NavKey

@Serializable
data class EditAccount(val id: Long): NavKey

@Serializable
data class EditCategory(val id: Long): NavKey

@Serializable
data object Onboarding: NavKey

@Serializable
data class Account(val id: Long): NavKey

@Serializable
data class Transaction(val id: Long): NavKey

@Serializable
data object Categories: NavKey

@Serializable
data object Debts: NavKey

@Serializable
data object Import: NavKey

@Serializable
data object Settings: NavKey

@Serializable
data object PieChart: NavKey