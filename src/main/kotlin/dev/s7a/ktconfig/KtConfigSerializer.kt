package dev.s7a.ktconfig

import kotlin.reflect.KType

/**
 * User-defined type serializer.
 *
 * @property T Config-storable type.
 * @property Z User-defined type.
 * @since 1.0.0
 */
interface KtConfigSerializer<T, Z> {
    /**
     * ```
     * typeOf<T>()
     * ```
     *
     * @since 1.0.0
     */
    val type: KType

    /**
     * Convert config-storable type to user-defined type.
     *
     * @since 1.0.0
     */
    fun deserialize(value: T): Z?

    /**
     * Convert user-defined type to config-storable type.
     *
     * @since 1.0.0
     */
    fun serialize(value: Z): T?
}
