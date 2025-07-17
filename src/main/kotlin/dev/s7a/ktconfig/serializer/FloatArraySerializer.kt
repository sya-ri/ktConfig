package dev.s7a.ktconfig.serializer

/**
 * Serializer implementation for FloatArray collections.
 * Handles serialization and deserialization of FloatArray types using the provided value serializer.
 *
 * @since 2.0.0
 */
object FloatArraySerializer : CollectionSerializer<Float, FloatArray>(FloatSerializer) {
    override fun toCollection(value: List<Float>) = value.toFloatArray()

    override fun toList(value: FloatArray) = value.toList()
}
