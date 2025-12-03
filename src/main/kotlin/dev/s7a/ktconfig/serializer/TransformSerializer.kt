package dev.s7a.ktconfig.serializer

/**
 * Abstract serializer that transforms values between two types using a base serializer.
 * This class allows converting between type [T] and type [B] during serialization/deserialization.
 *
 * @param T The type to transform to/from
 * @param B The base type used by the underlying serializer
 * @property base The base serializer used for the underlying type B
 * @since 2.0.0
 */
abstract class TransformSerializer<T, B>(
    val base: Serializer<B>,
) : Serializer<T> {
    /**
     * A keyable serializer that transforms values between two types using a base keyable serializer.
     * This class allows converting between type [T] and type [B] during serialization/deserialization,
     * where both types can be used as Map keys.
     *
     * @param T The type to transform to/from
     * @param B The base type used by the underlying serializer
     * @property base The base keyable serializer used for the underlying type B
     * @since 2.0.0
     */
    abstract class Keyable<T, B>(
        base: Serializer.Keyable<B>,
    ) : TransformSerializer<T, B>(base),
        Serializer.Keyable<T>

    override fun deserialize(value: Any) = decode(base.deserialize(value))

    override fun serialize(value: T) = base.serialize(encode(value))

    /**
     * Transforms a value of base type B into a value of target type T.
     * This method is used during deserialization to convert from the base type to the desired type.
     *
     * @param value The value of base type B to transform
     * @return The transformed value of target type T
     * @since 2.0.0
     */
    abstract fun decode(value: B): T

    /**
     * Transforms a value of target type T into a value of base type B.
     * This method is used during serialization to convert back to the base type.
     *
     * @param value The value of target type T to transform
     * @return The transformed value of base type B
     * @since 2.0.0
     */
    abstract fun encode(value: T): B
}
