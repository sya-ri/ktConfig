package dev.s7a.ktconfig.serializer

/**
 * A serializer that transforms values between two types using value conversion functions.
 * This class allows converting between type T and type B during serialization/deserialization
 * by providing explicit conversion functions.
 *
 * @param T The type to transform to/from
 * @param B The base type used by the underlying serializer
 * @param base The base serializer used for the underlying type B
 * @param toClass Function to convert from base type B to target type T
 * @param toValue Function to convert from target type T back to base type B
 * @since 2.0.0
 */
class ValueSerializer<T, B>(
    base: Serializer<B>,
    private val toClass: (B) -> T,
    private val toValue: (T) -> B,
) : TransformSerializer<T, B>(base) {
    /**
     * A keyable serializer that transforms values between two types using value conversion functions.
     * This class allows converting between type T and type B during serialization/deserialization
     * where both types can be used as Map keys.
     *
     * @param T The type to transform to/from
     * @param B The base type used by the underlying serializer
     * @param base The base keyable serializer used for the underlying type B
     * @param toClass Function to convert from base type B to target type T
     * @param toValue Function to convert from target type T back to base type B
     * @since 2.0.0
     */
    class Keyable<T, B>(
        base: Serializer.Keyable<B>,
        private val toClass: (B) -> T,
        private val toValue: (T) -> B,
    ) : TransformSerializer.Keyable<T, B>(base) {
        /**
         * Transforms a value from the base type to the target type using the toClass function.
         *
         * @param value The value of base type to transform
         * @return The transformed value of target type
         * @since 2.0.0
         */
        override fun transform(value: B) = toClass(value)

        /**
         * Transforms a value from the target type back to the base type using the toValue function.
         *
         * @param value The value of target type to transform back
         * @return The transformed value of base type
         * @since 2.0.0
         */
        override fun transformBack(value: T) = toValue(value)
    }

    /**
     * Transforms a value from the base type to the target type using the toClass function.
     *
     * @param value The value of base type to transform
     * @return The transformed value of target type
     * @since 2.0.0
     */
    override fun transform(value: B) = toClass(value)

    /**
     * Transforms a value from the target type back to the base type using the toValue function.
     *
     * @param value The value of target type to transform back
     * @return The transformed value of base type
     * @since 2.0.0
     */
    override fun transformBack(value: T) = toValue(value)
}
