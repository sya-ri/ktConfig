package dev.s7a.example.serializer

import dev.s7a.ktconfig.UseSerializer
import dev.s7a.ktconfig.serializer.Serializer
import dev.s7a.ktconfig.serializer.StringSerializer

typealias IncorrectString =
    @UseSerializer(IncorrectSerializer::class)
    String

typealias OverrideIncorrectString =
    @UseSerializer(StringSerializer::class)
    IncorrectString

object IncorrectSerializer : Serializer<Any> {
    override fun deserialize(value: Any): Any {
        error("This serializer is incorrect")
    }

    override fun serialize(value: Any): Any {
        error("This serializer is incorrect")
    }
}
