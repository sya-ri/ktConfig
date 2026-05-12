package dev.s7a.ktconfig

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
