package dev.s7a.ktconfig

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfigurationOptions

/**
 * Internal utility object for managing YAML configuration comments through reflection.
 * Handles compatibility between different Bukkit versions for comment-related operations.
 *
 * @since 2.0.0
 */
internal object Reflection {
    /**
     * Functional interface for setting header comments in YAML configuration.
     * Provides version-compatible method for setting header comments.
     *
     * @since 2.0.0
     */
    private fun interface SetHeaderCommentMethod {
        fun invoke(
            options: YamlConfigurationOptions,
            comments: List<String>,
        )
    }

    /**
     * Functional interface for setting value comments in YAML configuration.
     * Provides version-compatible method for setting comments on specific configuration paths.
     *
     * @since 2.0.0
     */
    private fun interface SetValueCommentMethod {
        fun invoke(
            section: ConfigurationSection,
            path: String,
            comment: List<String>?,
        )
    }

    private val setHeaderCommentMethod: SetHeaderCommentMethod
    private val setValueCommentMethod: SetValueCommentMethod

    init {
        val optionsClass = YamlConfigurationOptions::class.java
        setHeaderCommentMethod =
            try {
                val method = optionsClass.getMethod("setHeader", List::class.java)
                SetHeaderCommentMethod { options, comments ->
                    method.invoke(options, comments)
                }
            } catch (_: NoSuchMethodException) {
                val method = optionsClass.getMethod("header", String::class.java)
                SetHeaderCommentMethod { options, comments ->
                    method.invoke(options, comments.joinToString("\n"))
                }
            }
        val sectionClass = ConfigurationSection::class.java
        setValueCommentMethod =
            try {
                val method = sectionClass.getMethod("setComments", String::class.java, List::class.java)
                SetValueCommentMethod { section, path, comments ->
                    method.invoke(section, path, comments)
                }
            } catch (_: NoSuchMethodException) {
                SetValueCommentMethod { _, _, _ -> } // Unsupported old version
            }
    }

    /**
     * Sets the header comment for a YAML configuration.
     *
     * @param options The YAML configuration options
     * @param comment List of strings representing the header comment, or null to remove
     * @since 2.0.0
     */
    fun setHeaderComment(
        options: YamlConfigurationOptions,
        comment: List<String>?,
    ) {
        if (comment != null) {
            setHeaderCommentMethod.invoke(options, comment)
        }
    }

    /**
     * Sets a comment for a specific path in the configuration.
     *
     * @param section The configuration section
     * @param path The path to set the comment for
     * @param comment List of strings representing the comment, or null to remove
     * @since 2.0.0
     */
    fun setComment(
        section: ConfigurationSection,
        path: String,
        comment: List<String>?,
    ) {
        setValueCommentMethod.invoke(section, path, comment)
    }
}
