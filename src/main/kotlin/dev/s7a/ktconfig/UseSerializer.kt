package dev.s7a.ktconfig

import kotlin.reflect.KClass

/**
 * Use a user-defined type serializer.
 *
 * @property with Class that extends [KtConfigSerializer]
 * @since 1.0.0
 */
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
annotation class UseSerializer(
    val with: KClass<out KtConfigSerializer<*, *>>,
)
