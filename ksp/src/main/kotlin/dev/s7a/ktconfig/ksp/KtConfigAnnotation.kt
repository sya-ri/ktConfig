package dev.s7a.ktconfig.ksp

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSType

/**
 * Represents a @KtConfig annotation with its configuration parameters.
 *
 * @property hasDefault Whether the configuration has default values
 * @property discriminator The discriminator character used for configuration keys (defaults to "$")
 */
data class KtConfigAnnotation(
    val hasDefault: Boolean,
    val discriminator: String,
) {
    companion object {
        /**
         * Checks if the sequence contains a @KtConfig annotation.
         *
         * @return True if @KtConfig annotation is present, false otherwise
         */
        fun KSAnnotated.getKtConfigAnnotation() =
            annotations.firstNotNullOfOrNull { annotation ->
                if (annotation.shortName.asString() != "KtConfig") return@firstNotNullOfOrNull null
                val arguments = annotation.arguments.associate { it.name?.asString() to it.value }
                KtConfigAnnotation(
                    hasDefault = arguments["hasDefault"] as? Boolean ?: false,
                    discriminator = (arguments["discriminator"] as? String).orEmpty().ifBlank { "$" },
                )
            }
    }

    /**
     * Represents a @Comment annotation with its content lines.
     *
     * @property content The list of comment lines
     */
    data class Comment(
        val content: List<String>,
    ) {
        companion object {
            /**
             * Checks if the sequence contains a @Comment annotation and extracts its content.
             *
             * @return Comment instance with the content lines if @Comment annotation is present, null otherwise
             */
            fun KSAnnotated.getCommentAnnotation() =
                annotations.firstNotNullOfOrNull { annotation ->
                    if (annotation.shortName.asString() != "Comment") return@firstNotNullOfOrNull null
                    val arguments = annotation.arguments.associate { it.name?.asString() to it.value }
                    val content = arguments["content"] as? List<*> ?: return@firstNotNullOfOrNull null
                    if (content.isEmpty()) return@firstNotNullOfOrNull null
                    if (content.first() !is String) return@firstNotNullOfOrNull null
                    Comment(content.map(Any?::toString))
                }
        }
    }

    /**
     * Represents a @SerialName annotation with its name value.
     *
     * @property name The serial name value
     */
    data class SerialName(
        val name: String,
    ) {
        companion object {
            /**
             * Checks if the sequence contains a @SerialName annotation and extracts its value.
             *
             * @return SerialName instance with the name value if @SerialName annotation is present, null otherwise
             */
            fun KSAnnotated.getSerialNameAnnotation() =
                annotations.firstNotNullOfOrNull { annotation ->
                    if (annotation.shortName.asString() != "SerialName") return@firstNotNullOfOrNull null
                    val arguments = annotation.arguments.associate { it.name?.asString() to it.value }
                    val name = arguments["name"] as? String ?: return@firstNotNullOfOrNull null
                    SerialName(name)
                }
        }
    }

    /**
     * Represents a @UseSerializer annotation with its serializer type.
     *
     * @property serializer The KSType of the custom serializer
     */
    data class UseSerializer(
        val serializer: KSType,
    ) {
        companion object {
            /**
             * Helper function to extract @UseSerializer annotation from a sequence of annotations.
             *
             * @return UseSerializer instance if @UseSerializer annotation is present, null otherwise
             */
            private fun Sequence<KSAnnotation>.get() =
                firstNotNullOfOrNull { annotation ->
                    if (annotation.shortName.asString() != "UseSerializer") return@firstNotNullOfOrNull null
                    val arguments = annotation.arguments.associate { it.name?.asString() to it.value }
                    val serializer = arguments["serializer"] ?: return@firstNotNullOfOrNull null
                    if (serializer is KSType) {
                        UseSerializer(serializer)
                    } else {
                        null
                    }
                }

            /**
             * Extracts @UseSerializer annotation from a KSType.
             *
             * @return UseSerializer instance if @UseSerializer annotation is present, null otherwise
             */
            fun KSType.getUseSerializerAnnotation() = annotations.get()

            /**
             * Extracts @UseSerializer annotation from a KSAnnotated element.
             *
             * @return UseSerializer instance if @UseSerializer annotation is present, null otherwise
             */
            fun KSAnnotated.getUseSerializerAnnotation() = annotations.get()
        }
    }
}
