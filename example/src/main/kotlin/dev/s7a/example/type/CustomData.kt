package dev.s7a.example.type

import dev.s7a.ktconfig.UseSerializer
import dev.s7a.ktconfig.serializer.IntSerializer
import dev.s7a.ktconfig.serializer.TransformSerializer

@UseSerializer(CustomData.Serializer::class)
data class CustomData(
    val value: Int,
) {
    object Serializer : TransformSerializer<CustomData, Int>(IntSerializer) {
        override fun transform(value: Int) = CustomData(value)

        override fun transformBack(value: CustomData) = value.value
    }
}
