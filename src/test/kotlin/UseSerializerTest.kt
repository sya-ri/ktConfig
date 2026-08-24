import dev.s7a.ktconfig.KtConfig
import dev.s7a.ktconfig.UseSerializer
import dev.s7a.ktconfig.serializer.IntSerializer
import dev.s7a.ktconfig.serializer.TransformSerializer
import dev.s7a.ktconfig.type.FormattedVectorSerializer
import org.bukkit.util.Vector
import kotlin.test.Test
import kotlin.test.assertEquals

@UseSerializer(CustomSerializerData.Serializer::class)
data class CustomSerializerData(
    val value: Int,
) {
    object Serializer : TransformSerializer<CustomSerializerData, Int>(IntSerializer) {
        override fun decode(value: Int) = CustomSerializerData(value)

        override fun encode(value: CustomSerializerData) = value.value
    }
}

@KtConfig
data class TypeUseSerializerConfig(
    val value: CustomSerializerData,
)

@KtConfig
data class PropertyUseSerializerConfig(
    val value:
        @UseSerializer(FormattedVectorSerializer::class)
        Vector,
)

class UseSerializerTest {
    @Test
    fun testGeneratedLoaderUsesSerializerFromAnnotatedType() {
        val expected = TypeUseSerializerConfig(CustomSerializerData(42))
        val yaml = TypeUseSerializerConfigLoader.saveToString(expected)

        assertEquals(expected, TypeUseSerializerConfigLoader.loadFromString(yaml))
    }

    @Test
    fun testGeneratedLoaderUsesSerializerFromAnnotatedProperty() {
        val expected = PropertyUseSerializerConfig(Vector(1.0, 2.0, 3.0))
        val yaml = PropertyUseSerializerConfigLoader.saveToString(expected)

        assertEquals(expected, PropertyUseSerializerConfigLoader.loadFromString(yaml))
    }
}
