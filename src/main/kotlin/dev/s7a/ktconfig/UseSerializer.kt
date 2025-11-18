package dev.s7a.ktconfig

import dev.s7a.ktconfig.serializer.Serializer
import kotlin.reflect.KClass

/**
 * Specifies a custom serializer to be used for a type.
 *
 * This annotation can be applied to types to define how they should be serialized and deserialized.
 * It is commonly used in conjunction with [Serializer] implementations to handle custom data types.
 *
 * **Note**: Only objects are supported for custom serialization, classes are not supported.
 *
 * @property serializer The [KClass] of the [Serializer] implementation to be used for the annotated type.
 * @since 2.0.0
 */
@Target(AnnotationTarget.TYPE)
@Repeatable
annotation class UseSerializer(
    val serializer: KClass<out Serializer<*>>,
)
