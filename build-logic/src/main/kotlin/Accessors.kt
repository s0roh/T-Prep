import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.gradle.api.Action
import com.android.build.gradle.LibraryExtension
import org.gradle.api.plugins.ExtensionAware
import com.android.build.gradle.BaseExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions

internal val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.android(configure: Action<LibraryExtension>): Unit =
    (this as ExtensionAware).extensions.configure("android", configure)

internal fun BaseExtension.kotlinOptions(configure: Action<KotlinJvmCompilerOptions>): Unit =
    (this as ExtensionAware).extensions.configure("compilerOptions", configure)
