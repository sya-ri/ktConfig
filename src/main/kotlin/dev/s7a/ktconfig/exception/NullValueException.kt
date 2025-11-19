package dev.s7a.ktconfig.exception

/**
 * Exception thrown when a null value is encountered during serialization or deserialization
 * where null values are not allowed.
 *
 * @since 2.0.0
 */
class NullValueException : KtConfigException("Must not be null")
