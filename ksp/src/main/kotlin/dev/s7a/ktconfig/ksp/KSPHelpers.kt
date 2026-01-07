package dev.s7a.ktconfig.ksp

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile

/**
 * Gets the full name of a class by traversing its parent hierarchy.
 * For nested classes, returns a list of class names from outermost to innermost.
 * For top-level classes, returns a single-element list with the class name.
 *
 * @param declaration The class declaration to get the full name for
 * @return List of class names representing the full hierarchy
 */
fun getFullName(declaration: KSClassDeclaration): List<String> =
    if (declaration.parent is KSFile) {
        listOf(declaration.simpleName.asString())
    } else {
        getFullName(declaration.parent as KSClassDeclaration) + declaration.simpleName.asString()
    }

/**
 * Generates a loader class name for the given class declaration.
 * Combines the full class name parts with underscores and appends "Loader".
 *
 * @param declaration The class declaration to generate a loader name for
 * @return The generated loader class name
 */
fun getLoaderName(declaration: KSClassDeclaration): String {
    val fullName = getFullName(declaration).joinToString("")
    return "${fullName}Loader"
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
