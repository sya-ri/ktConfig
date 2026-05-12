package dev.s7a.ktconfig

import dev.s7a.ktconfig.exception.InvalidDiscriminatorException
import dev.s7a.ktconfig.exception.InvalidFormatException
import dev.s7a.ktconfig.exception.KtConfigLoadException
import dev.s7a.ktconfig.exception.NotFoundValueException
import dev.s7a.ktconfig.exception.NullValueException
import dev.s7a.ktconfig.exception.UnsupportedConvertException

/**
 * Structured error reported by ktConfig validation and future aggregate loading APIs.
 *
 * @property path The configuration path or property name related to the error. Root-level errors use an empty string.
 * @property message A human-readable validation or loading error message.
 * @property kind The category of error.
 * @property cause Optional underlying exception that caused the error.
 * @since 2.2.0
 */
data class KtConfigError(
    val path: String,
    val message: String,
    val kind: Kind = Kind.InvalidValue,
    val cause: Throwable? = null,
) {
    companion object {
        /**
         * Converts a thrown loading exception into structured errors.
         *
         * @param path The path being loaded when [cause] was thrown.
         * @param cause The exception thrown while loading.
         * @return Structured loading errors.
         * @since 2.2.0
         */
        fun fromException(
            path: String,
            cause: Throwable,
        ): List<KtConfigError> =
            when (cause) {
                is KtConfigLoadException -> {
                    cause.errors
                }

                is NotFoundValueException -> {
                    listOf(KtConfigError(KtConfigLoader.formatPath(cause.path), "Not found value", Kind.NotFound, cause))
                }

                is NullValueException -> {
                    listOf(KtConfigError(KtConfigLoader.formatPath(path), cause.message, Kind.NullValue, cause))
                }

                is InvalidFormatException -> {
                    listOf(KtConfigError(KtConfigLoader.formatPath(path), cause.message, Kind.InvalidFormat, cause))
                }

                is InvalidDiscriminatorException -> {
                    listOf(KtConfigError(KtConfigLoader.formatPath(path), cause.message, Kind.InvalidDiscriminator, cause))
                }

                is UnsupportedConvertException -> {
                    listOf(KtConfigError(KtConfigLoader.formatPath(path), cause.message, Kind.UnsupportedConvert, cause))
                }

                else -> {
                    listOf(KtConfigError(KtConfigLoader.formatPath(path), cause.message ?: cause.toString(), Kind.Unknown, cause))
                }
            }
    }

    /**
     * Categories of errors that can be reported by ktConfig.
     *
     * @since 2.2.0
     */
    enum class Kind {
        /** A required value was not found. */
        NotFound,

        /** A non-null value was explicitly null. */
        NullValue,

        /** A value could not be parsed from its serialized form. */
        InvalidFormat,

        /** A sealed class discriminator did not match any known subtype. */
        InvalidDiscriminator,

        /** A value could not be converted to the requested type. */
        UnsupportedConvert,

        /** A user-defined validation rule failed. */
        InvalidValue,

        /** An unknown or uncategorized error occurred. */
        Unknown,
    }
}

/**
 * Formats loading or validation errors as a human-readable message.
 *
 * @since 2.2.0
 */
fun List<KtConfigError>.format(): String =
    buildString {
        append("Failed to load config (")
        append(size)
        append(if (size == 1) " error" else " errors")
        append("):")
        this@format.forEach { error ->
            appendLine()
            append("- ")
            if (error.path.isNotEmpty()) {
                append("[")
                append(error.path)
                append("] ")
            }
            append(error.message)
        }
    }
