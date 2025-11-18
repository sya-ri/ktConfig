package dev.s7a.example.serializer

import dev.s7a.ktconfig.UseSerializer
import dev.s7a.ktconfig.serializer.StringSerializer
import dev.s7a.ktconfig.serializer.TransformSerializer
import org.bukkit.util.Vector

typealias FormattedVector =
    @UseSerializer(FormattedVectorSerializer::class)
    Vector

object FormattedVectorSerializer : TransformSerializer<Vector, String>(StringSerializer) {
    override fun transform(value: String): Vector {
        val (x, y, z) = value.split(",").map(String::toDouble)
        return Vector(x, y, z)
    }

    override fun transformBack(value: Vector): String = "${value.x},${value.y},${value.z}"
}
