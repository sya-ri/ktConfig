package dev.s7a.ktconfig.serializer

abstract class TransformSerializer<T, B>(
    val base: ValueSerializer<B>,
) : ValueSerializer<T> {
    override fun deserialize(value: Any) = transform(base.deserialize(value))

    override fun serialize(value: T) = base.serialize(transformBack(value))

    abstract fun transform(value: B): T

    abstract fun transformBack(value: T): B
}
