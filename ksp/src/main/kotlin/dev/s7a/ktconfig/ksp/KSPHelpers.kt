package dev.s7a.ktconfig.ksp

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSTypeAlias
import dev.s7a.ktconfig.ksp.KtConfigAnnotation.Companion.getKtConfigAnnotation

/**
 * Gets the full name of a class by traversing its parent hierarchy.
 * For nested classes, returns a list of class names from outermost to innermost.
 * For top-level classes, returns a single-element list with the class name.
 *
 * @param declaration The class declaration to get the full name for
 * @return List of class names representing the full hierarchy
 */
fun getFullName(declaration: KSClassDeclaration): List<String> =
    when (val parent = declaration.parent) {
        is KSClassDeclaration -> getFullName(parent) + declaration.simpleName.asString()
        else -> listOf(declaration.simpleName.asString())
    }

/**
 * Retrieves the loader name for the given class declaration based on the presence
 * of a `@KtConfig` annotation. It replaces the placeholder `{CLASS_NAME}` in the
 * `loaderName` value of the annotation with the full class name.
 *
 * @param declaration The class declaration for which the loader name should be generated.
 * @return The loader name as a string if the `@KtConfig` annotation is present;
 *         otherwise, returns `null`.
 */
fun getLoaderName(declaration: KSClassDeclaration): String? {
    val fullName = getFullName(declaration).joinToString("")
    val annotation = declaration.getKtConfigAnnotation() ?: return null
    return annotation.loaderName.replace("{CLASS_NAME}", fullName)
}

fun getLoaderName(declaration: KSTypeAlias): String? {
    val annotation = declaration.getKtConfigAnnotation() ?: return null
    return annotation.loaderName.replace("{CLASS_NAME}", declaration.simpleName.asString())
}

/**
 * Recursively retrieves all sealed subclasses of this class declaration.
 * For sealed classes with nested sealed subclasses, this function traverses the entire hierarchy
 * and returns only the leaf (non-sealed or final sealed) subclasses.
 *
 * @receiver The sealed class declaration to get subclasses from
 * @return List of all leaf sealed subclasses in the hierarchy
 */
fun KSClassDeclaration.getSealedSubclassesDeeply(): List<KSClassDeclaration> {
    val subclasses = getSealedSubclasses().toList()
    return subclasses.flatMap {
        it.getSealedSubclassesDeeply().ifEmpty { listOf(it) }
    }
}
