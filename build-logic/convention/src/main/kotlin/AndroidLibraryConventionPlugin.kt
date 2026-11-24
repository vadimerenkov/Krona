import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import vadimerenkov.krona.convention.getVersion
import vadimerenkov.krona.convention.libs
import vadimerenkov.krona.convention.pathToPackageName

class AndroidLibraryConventionPlugin: Plugin<Project> {

	override fun apply(target: Project) {

		with (target) {
			with (pluginManager) {
				apply("com.android.library")
			}

			extensions.configure<LibraryExtension>() {
				namespace = pathToPackageName()
				compileSdk = libs.getVersion("android-compileSdk").toInt()

				defaultConfig {
					minSdk = libs.getVersion("android-minSdk").toInt()
				}
				compileOptions {
					sourceCompatibility = JavaVersion.VERSION_17
					targetCompatibility = JavaVersion.VERSION_17
				}
				buildFeatures {
					buildConfig = true
				}
			}
		}
	}
}