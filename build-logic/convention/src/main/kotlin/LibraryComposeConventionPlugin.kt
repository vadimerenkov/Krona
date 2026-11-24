import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class LibraryComposeConventionPlugin: Plugin<Project> {

	override fun apply(target: Project) {
		target.run {
			with(pluginManager) {
				apply("krona.convention.android.library")
				apply("org.jetbrains.kotlin.plugin.compose")
			}
			val extension = extensions.getByType<LibraryExtension>()
			extension.run {
				buildFeatures {
					compose = true
				}
			}
		}
	}

}