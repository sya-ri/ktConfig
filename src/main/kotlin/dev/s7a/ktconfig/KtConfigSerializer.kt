package dev.s7a.ktconfig

import kotlin.reflect.KType

/**
 * User-defined type serializer.
 *
 * @since 1.0.0
 */
interface KtConfigSerializer {
    /**
     * Config-storable type.
     *
     * @since 1.0.0
     */
    val type: KType

    /**
     * Convert config-storable type to user-defined type.
     *
     * @param value Type is [type].
     * @return User-defined type.
     * @since 1.0.0
     */
    fun deserialize(value: Any?): Any?

    /**
     * Convert user-defined type to config-storable type.
     *
     * @param value Type is [deserialize] return type.
     * @return Type is [type].
     * @since 1.0.0
     */
    fun serialize(value: Any?): Any?
}
