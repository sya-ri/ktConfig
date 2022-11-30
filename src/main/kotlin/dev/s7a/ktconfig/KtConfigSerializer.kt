package dev.s7a.ktconfig

import kotlin.reflect.KType

interface KtConfigSerializer {
    val type: KType

    fun deserialize(value: Any?): Any?

    fun serialize(value: Any?): Any?
}
