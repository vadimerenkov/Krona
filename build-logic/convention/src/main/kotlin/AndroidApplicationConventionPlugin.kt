import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import vadimerenkov.krona.convention.getVersion
import vadimerenkov.krona.convention.libs

class AndroidApplicationConventionPlugin: Plugin<Project> {

	override fun apply(target: Project) {
		with (target) {
			with (pluginManager) {
				apply("com.android.application")
			}

			extensions.configure<ApplicationExtension>() {
				namespace = "vadimerenkov.krona"
				compileSdk = libs.getVersion("android-compileSdk").toInt()

				defaultConfig {
					applicationId = "vadimerenkov.krona"
					minSdk = libs.getVersion("android-minSdk").toInt()
					targetSdk = libs.getVersion("android-targetSdk").toInt()
					versionCode = libs.getVersion("version-code").toInt()
					versionName = libs.getVersion("version-name")
				}
				packaging {
					resources {
						excludes += "/META-INF/{AL2.0,LGPL2.1}"
					}
				}
				buildTypes {
					getByName("release") {
						isMinifyEnabled = true
						isShrinkResources = true
						proguardFiles(
							getDefaultProguardFile("proguard-android-optimize.txt"),
							"proguard-rules.pro"
						)
					}
				}
				compileOptions {
					sourceCompatibility = JavaVersion.VERSION_17
					targetCompatibility = JavaVersion.VERSION_17
				}
				dependenciesInfo {
					includeInApk = false
					includeInBundle = false
				}
				buildFeatures {
					buildConfig = true
				}
				bundle {
					language {
						enableSplit = false
					}
				}
				androidResources {
					generateLocaleConfig = true
				}
			}
		}
	}
}