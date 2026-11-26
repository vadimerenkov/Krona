import java.util.Properties

plugins {
	alias(libs.plugins.convention.android.library)
	alias(libs.plugins.room)
	alias(libs.plugins.ksp)
	alias(libs.plugins.serialization)
}

dependencies {
	implementation(libs.androidx.room.runtime)
	implementation(libs.androidx.sqlite.bundled)
	implementation(libs.koin.core)
	implementation(libs.koin.android)
	implementation(libs.apache.commons.csv)
	implementation(libs.androidx.paging.common)
	implementation(libs.androidx.room.paging)
	implementation(libs.kotlinx.serialization)
	implementation(libs.bundles.ktor)

	ksp(libs.androidx.room.compiler)

	implementation(projects.core.domain)
}

android {
	val localProperties = Properties().apply {
		val file = rootProject.file("local.properties")
		if (file.exists()) {
			file.inputStream().use { load(it) }
		}
	}
	defaultConfig {
		val apiKey = localProperties.getProperty("EXCHANGE_RATES_API_KEY") ?: ""
		buildConfigField("String", "EXCHANGE_RATES_API_KEY", apiKey)
	}
}

room {
	schemaDirectory("$projectDir/schemas")
}