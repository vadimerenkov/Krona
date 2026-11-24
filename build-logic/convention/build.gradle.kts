
plugins {
	`kotlin-dsl`
}

group = "vadimerenkov.krona.buildlogic"

dependencies {
	compileOnly(libs.android.gradlePlugin)
	compileOnly(libs.android.tools.common)
	compileOnly(libs.kotlin.gradlePlugin)
	compileOnly(libs.ksp.gradlePlugin)
	compileOnly(libs.room.gradlePlugin)
}

gradlePlugin {
	plugins {
		register("androidApplication") {
			id = "krona.convention.android.application"
			implementationClass = "AndroidApplicationConventionPlugin"
		}
		register("composeApplication") {
			id = "krona.convention.android.compose.application"
			implementationClass = "ApplicationComposeConventionPlugin"
		}
		register("composeLibrary") {
			id = "krona.convention.android.compose.library"
			implementationClass = "LibraryComposeConventionPlugin"
		}
		register("androidLibrary") {
			id = "krona.convention.android.library"
			implementationClass = "AndroidLibraryConventionPlugin"
		}
	}
}