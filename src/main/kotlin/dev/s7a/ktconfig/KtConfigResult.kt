package dev.s7a.ktconfig

import dev.s7a.ktconfig.exception.KtConfigLoadException

/**
 * Result of loading or validating a ktConfig value.
 *
 * @param T The loaded value type.
 * @since 2.2.0
 */
sealed class KtConfigResult<out T> {
    /**
     * Successful result containing the loaded [value].
     *
     * @since 2.2.0
     */
    data class Success<T>(
        val value: T,
    ) : KtConfigResult<T>()

    /**
     * Failed result containing all collected [errors].
     *
     * @since 2.2.0
     */
    data class Failure(
        val errors: List<KtConfigError>,
    ) : KtConfigResult<Nothing>()

    /**
     * Returns the value or throws [KtConfigLoadException] when this result is a failure.
     *
     * @since 2.2.0
     */
    fun getOrThrow(): T =
        when (this) {
            is Success -> value
            is Failure -> throw KtConfigLoadException(errors)
        }
}
