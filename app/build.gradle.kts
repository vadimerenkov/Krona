plugins {
	alias(libs.plugins.convention.compose.application)
}

dependencies {
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.activity.compose)
	implementation(libs.androidx.compose.material3)
	implementation(libs.androidx.compose.ui)
	implementation(libs.androidx.compose.ui.graphics)
	implementation(libs.androidx.compose.ui.tooling.preview)
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.bundles.koin)
	implementation(libs.koin.android)
	implementation(libs.androidx.datastore)
	implementation(libs.androidx.datastore.preferences)

	implementation(projects.core.data)
	implementation(projects.core.domain)
	implementation(projects.core.presentation)
}