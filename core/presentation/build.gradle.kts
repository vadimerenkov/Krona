plugins {
	alias(libs.plugins.convention.compose.library)
	alias(libs.plugins.serialization)
}

dependencies {
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.activity.compose)
	implementation(libs.androidx.compose.material3)
	implementation(libs.androidx.compose.ui)
	implementation(libs.androidx.compose.ui.graphics)
	implementation(libs.material.icons.extended)
	implementation(libs.bundles.koin)
	implementation(libs.bundles.navigation3)
	implementation(libs.androidx.paging.common)
	implementation(libs.androidx.paging.compose)
	implementation(libs.vico.compose)
	implementation(libs.vico.compose.m3)
	implementation(libs.androidx.datastore)
	implementation(libs.androidx.datastore.preferences)

	implementation(libs.androidx.compose.ui.tooling.preview)
	debugImplementation(libs.androidx.compose.ui.tooling)

	implementation(projects.core.domain)

}

android {
	defaultConfig {
		buildConfigField("String", "VERSION_NAME", "\"${libs.versions.version.name.get()}\"")
	}
}