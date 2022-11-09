package dev.s7a.ktconfig.internal

import org.bukkit.configuration.file.YamlConfigurationOptions

internal object YamlConfigurationOptionsReflection {
    private fun interface SetHeaderCommentMethod {
        fun apply(options: YamlConfigurationOptions, comments: List<String>)
    }

    private val setHeaderCommentMethod: SetHeaderCommentMethod

    init {
        val clazz = YamlConfigurationOptions::class.java
        setHeaderCommentMethod = try {
            val method = clazz.getMethod("setHeader", List::class.java)
            SetHeaderCommentMethod { options, comments ->
                method.invoke(options, comments)
            }
        } catch (_: NoSuchMethodException) {
            val method = clazz.getMethod("header", String::class.java)
            SetHeaderCommentMethod { options, comments ->
                method.invoke(options, comments.joinToString("\n"))
            }
        }
    }

    fun YamlConfigurationOptions.setHeaderComment(comment: List<String>?) {
        if (comment != null) {
            setHeaderCommentMethod.apply(this, comment)
        }
    }
}
