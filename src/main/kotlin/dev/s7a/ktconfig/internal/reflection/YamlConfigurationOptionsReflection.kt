package dev.s7a.ktconfig.internal.reflection

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfigurationOptions

internal object YamlConfigurationOptionsReflection {
    private fun interface SetHeaderCommentMethod {
        fun invoke(
            options: YamlConfigurationOptions,
            comments: List<String>,
        )
    }

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

    fun YamlConfigurationOptions.setHeaderComment(comment: List<String>?) {
        if (comment != null) {
            setHeaderCommentMethod.invoke(this, comment)
        }
    }

    fun ConfigurationSection.setComment(
        path: String,
        comment: List<String>?,
    ) {
        setValueCommentMethod.invoke(this, path, comment)
    }
}
