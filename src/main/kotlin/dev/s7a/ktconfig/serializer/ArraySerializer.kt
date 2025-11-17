package dev.s7a.ktconfig.serializer

import java.lang.reflect.Array as ReflectArray

/**
 * Serializer implementation for [Array] collections.
 * Handles serialization and deserialization of [Array]<[T]> types using the provided value serializer.
 *
 * @param T The type of elements in the array
 * @param clazz The Class object representing type T
 * @param valueSerializer The serializer used to handle individual array elements
 * @since 2.0.0
 */
class ArraySerializer<T>(
    private val clazz: Class<T>,
    valueSerializer: Serializer<T>,
) : CollectionSerializer<T, Array<T>>(valueSerializer) {
    companion object {
        inline operator fun <reified T> invoke(valueSerializer: Serializer<T>) = ArraySerializer(T::class.java, valueSerializer)
    }

    /**
     * Nullable variant of [ArraySerializer] that allows null elements.
     *
     * @param T The type of elements in the array
     * @param clazz The Class object representing type T
     * @param valueSerializer The serializer used to handle individual array elements
     * @since 2.0.0
     */
    class Nullable<T>(
        private val clazz: Class<T>,
        valueSerializer: Serializer<T>,
    ) : CollectionSerializer.Nullable<T, Array<T?>>(valueSerializer) {
        companion object {
            inline operator fun <reified T> invoke(valueSerializer: Serializer<T>) = Nullable(T::class.java, valueSerializer)
        }

        @Suppress("UNCHECKED_CAST")
        override fun toCollection(value: List<T?>) =
            ReflectArray.newInstance(clazz, value.size).apply {
                value.forEachIndexed { index, item ->
                    ReflectArray.set(this, index, item)
                }
            } as Array<T?>

        override fun toList(value: Array<T?>) = value.toList()
    }

    @Suppress("UNCHECKED_CAST")
    override fun toCollection(value: List<T>) =
        ReflectArray.newInstance(clazz, value.size).apply {
            value.forEachIndexed { index, item ->
                ReflectArray.set(this, index, item)
            }
        } as Array<T>

    override fun toList(value: Array<T>): List<T> = value.toList()
}
