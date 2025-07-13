package dev.s7a.ktconfig.serializer

abstract class ExtendSerializer<T, B>(
    val base: ValueSerializer<B>,
) : ValueSerializer<T> {
    override fun from(value: Any) = convertFrom(base.from(value))

    override fun to(value: T) = base.to(convertTo(value))

    abstract fun convertFrom(value: B): T

    abstract fun convertTo(value: T): B
}
