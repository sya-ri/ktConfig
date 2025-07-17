package dev.s7a.ktconfig.serializer

/**
 * Abstract serializer that transforms values between two types using a base serializer.
 * This class allows converting between type T and type B during serialization/deserialization.
 *
 * @param T The type to transform to/from
 * @param B The base type used by the underlying serializer
 * @property base The base serializer used for the underlying type B
 * @since 2.0.0
 */
abstract class TransformSerializer<T, B>(
    val base: Serializer<B>,
) : Serializer<T> {
    override fun deserialize(value: Any) = transform(base.deserialize(value))

    override fun serialize(value: T) = base.serialize(transformBack(value))

    /**
     * Transforms a value from the base type to the target type.
     *
     * @param value The value of base type to transform
     * @return The transformed value of target type
     * @since 2.0.0
     */
    abstract fun transform(value: B): T

    /**
     * Transforms a value from the target type back to the base type.
     *
     * @param value The value of target type to transform back
     * @return The transformed value of base type
     * @since 2.0.0
     */
    abstract fun transformBack(value: T): B
}
