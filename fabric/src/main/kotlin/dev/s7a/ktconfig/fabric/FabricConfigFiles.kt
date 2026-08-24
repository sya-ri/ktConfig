package dev.s7a.ktconfig.fabric

import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Helpers for resolving files below Fabric Loader's configuration directory.
 *
 * @since 2.3.0
 */
object FabricConfigFiles {
    /**
     * Resolves [fileName] against Fabric Loader's configuration directory.
     *
     * ```kotlin
     * val file = FabricConfigFiles.resolve("example.yml").toFile()
     * ```
     *
     * @param fileName The file name or relative path to resolve
     * @return The resolved configuration path
     * @throws IllegalArgumentException if [fileName] is absolute or escapes the configuration directory
     * @throws java.nio.file.InvalidPathException if [fileName] is not a valid path
     * @since 2.3.0
     */
    fun resolve(fileName: String): Path {
        val relativePath = Paths.get(fileName)
        require(relativePath.isAbsolute.not()) { "fileName must be relative to the Fabric configuration directory" }
        val configDirectory = FabricLoader.getInstance().configDir.normalize()
        val resolvedPath = configDirectory.resolve(relativePath).normalize()
        require(resolvedPath.startsWith(configDirectory)) { "fileName must stay within the Fabric configuration directory" }
        return resolvedPath
    }
}
