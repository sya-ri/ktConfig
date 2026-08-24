package dev.s7a.ktconfig.fabric

import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.yaml.NodeStyle
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.IOException
import java.io.StringReader
import java.io.StringWriter

/**
 * YAML configuration document used by the Fabric backend.
 *
 * Nested paths are separated with [dev.s7a.ktconfig.KtConfigLoader.PATH_SEPARATOR]. Raw maps and lists are converted
 * recursively, and explicit YAML null values are preserved. Use [contains] to distinguish an explicit null from a
 * missing path.
 *
 * @since 2.3.0
 */
class YamlConfiguration private constructor(
    internal var root: CommentedConfigurationNode,
    private val pathSeparator: Char,
) {
    private var headerComment: List<String> = emptyList()
    private val comments = linkedMapOf<String, List<String>>()

    /**
     * Creates an empty YAML configuration.
     *
     * @since 2.3.0
     */
    constructor() : this(emptyLoader().createNode(), DEFAULT_PATH_SEPARATOR)

    internal constructor(pathSeparator: Char) : this(emptyLoader().createNode(), pathSeparator)

    /**
     * Returns whether a value, including an explicit null, exists at [path].
     *
     * @param path The configuration path to inspect
     * @return True when the path exists
     * @since 2.3.0
     */
    fun contains(path: String): Boolean = !node(path).virtual()

    /**
     * Returns the raw value stored at [path].
     *
     * Map and list nodes are converted recursively to Kotlin collections. Both a missing path and an explicit null
     * return null; use [contains] when that distinction is required.
     *
     * @param path The configuration path to read
     * @return The stored value, or null when the path is missing or explicitly null
     * @since 2.3.0
     */
    fun get(path: String): Any? = node(path).toValue()

    /**
     * Stores [value] at [path].
     *
     * Maps, iterables, and arrays are written recursively. A null value is stored as an explicit YAML null.
     *
     * @param path The configuration path to write
     * @param value The value to store
     * @since 2.3.0
     */
    fun set(
        path: String,
        value: Any?,
    ) {
        node(path).setValue(value)
    }

    /**
     * Sets a comment for [path].
     *
     * @param path The configuration path to annotate
     * @param comment The comment lines
     * @since 2.3.0
     */
    fun setComment(
        path: String,
        comment: List<String>,
    ) {
        comments[path] = comment
    }

    /**
     * Sets the document header comment.
     *
     * @param comment The header comment lines
     * @since 2.3.0
     */
    fun setHeaderComment(comment: List<String>) {
        headerComment = comment
    }

    /**
     * Loads YAML from [file], replacing the current document.
     *
     * @param file The YAML file to load
     * @throws IOException if the file cannot be read
     * @throws org.spongepowered.configurate.loader.ParsingException if the YAML is malformed
     * @since 2.3.0
     */
    fun load(file: File) {
        loadFromString(file.readText())
    }

    /**
     * Loads YAML from [content], replacing the current document.
     *
     * @param content The YAML content to load
     * @throws org.spongepowered.configurate.loader.ParsingException if the YAML is malformed
     * @since 2.3.0
     */
    fun loadFromString(content: String) {
        val preservedNulls =
            content.replace(
                Regex("([:\\[,\\-]\\s+)(?:null|~)(?=\\s*[,}\\]\\r\\n#]|\\s*$)"),
                "$1$NULL_SENTINEL",
            )
        val loader =
            configuredLoader()
                .source { BufferedReader(StringReader(preservedNulls)) }
                .build()
        root = loader.load()
    }

    /**
     * Saves this configuration to [file].
     *
     * Missing parent directories are created automatically.
     *
     * @param file The destination YAML file
     * @throws IOException if the file cannot be written
     * @since 2.3.0
     */
    fun save(file: File) {
        file.parentFile?.mkdirs()
        file.writeText(saveToString())
    }

    /**
     * Serializes this configuration to YAML.
     *
     * @return The serialized YAML content
     * @since 2.3.0
     */
    fun saveToString(): String {
        val output = StringWriter()
        val loader =
            configuredLoader()
                .sink { BufferedWriter(output) }
                .build()
        loader.save(root)
        val body = injectComments(output.toString().replace(NULL_SENTINEL, "null"))
        return if (headerComment.isEmpty()) {
            body
        } else {
            headerComment.joinToString("\n") { "# $it" } + "\n\n" + body
        }
    }

    private fun node(path: String): CommentedConfigurationNode {
        if (path.isEmpty()) return root
        val segments = path.split(pathSeparator).toTypedArray()
        return root.node(*segments)
    }

    private fun CommentedConfigurationNode.toValue(): Any? =
        when {
            isMap -> childrenMap().mapValues { (_, child) -> child.toValue() }
            isList -> childrenList().map { it.toValue() }
            else -> raw().let { if (it == NULL_SENTINEL) null else it }
        }

    private fun CommentedConfigurationNode.setValue(value: Any?) {
        when (value) {
            is Map<*, *> -> {
                raw(emptyMap<Any, Any>())
                value.forEach { (key, childValue) -> node(key ?: return@forEach).setValue(childValue) }
            }

            is Iterable<*> -> {
                raw(emptyList<Any>())
                value.forEach { appendListNode().setValue(it) }
            }

            is Array<*> -> {
                raw(emptyList<Any>())
                value.forEach { appendListNode().setValue(it) }
            }

            null -> {
                set(NULL_SENTINEL)
            }

            else -> {
                set(value)
            }
        }
    }

    private fun injectComments(yaml: String): String {
        if (comments.isEmpty()) return yaml
        val pathAtDepth = mutableListOf<String>()
        return yaml.lineSequence().joinToString("\n") { line ->
            val match = Regex("^(\\s*)([^#\\-][^:]*):").find(line) ?: return@joinToString line
            val indent = match.groupValues[1]
            val depth = indent.length / 2
            val key =
                match.groupValues[2]
                    .trim()
                    .removeSurrounding("'")
                    .removeSurrounding("\"")
            while (pathAtDepth.size > depth) pathAtDepth.removeAt(pathAtDepth.lastIndex)
            if (pathAtDepth.size == depth) pathAtDepth.add(key) else pathAtDepth[depth] = key
            val path = pathAtDepth.joinToString(pathSeparator.toString())
            val comment = comments[path] ?: return@joinToString line
            comment.joinToString("\n") { "$indent# $it" } + "\n" + line
        }
    }

    companion object {
        private const val DEFAULT_PATH_SEPARATOR = '\u0000'
        private const val NULL_SENTINEL = "ktconfig-explicit-null-4f28a19d"

        private fun configuredLoader() =
            YamlConfigurationLoader
                .builder()
                .nodeStyle(NodeStyle.BLOCK)
                .indent(2)
                .defaultOptions { it.implicitInitialization(false) }

        private fun emptyLoader() = configuredLoader().build()
    }
}
