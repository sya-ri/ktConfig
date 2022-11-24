package dev.s7a.ktconfig

interface KtConfigSerializer {
    fun deserialize(value: Any?): Any?

    fun serialize(value: Any?): Any?
}
