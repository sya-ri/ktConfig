package dev.s7a.ktconfig.serializer

/**
 * A simple serializer implementation that passes through values without any transformation.
 * This serializer is used as a fallback for handling arbitrary types that don't require specific serialization logic.
 *
 * **Note**: Values should be transformed to the correct type using [TransformSerializer].
 *
 * @since 2.0.0
 */
object AnySerializer : Serializer.Keyable<Any> {
    override fun deserialize(value: Any) = value

    override fun serialize(value: Any) = value
}
