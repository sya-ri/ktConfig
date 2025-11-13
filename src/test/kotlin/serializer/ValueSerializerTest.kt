package serializer

import dev.s7a.ktconfig.serializer.StringSerializer
import dev.s7a.ktconfig.serializer.ValueSerializer
import testSerializer
import kotlin.test.Test

@JvmInline
value class TestValue(
    val value: String,
)

class ValueSerializerTest {
    @Test
    fun testValueClass() =
        testSerializer(
            TestValue("hello"),
            ValueSerializer.Keyable(
                StringSerializer,
                { TestValue(it) },
                { it.value },
            ),
        )

    @Test
    fun testEmpty() =
        testSerializer(
            TestValue(""),
            ValueSerializer.Keyable(
                StringSerializer,
                { TestValue(it) },
                { it.value },
            ),
        )

    @Test
    fun testSpecialCharacters() =
        testSerializer(
            TestValue("!@#$%^&*()"),
            ValueSerializer.Keyable(
                StringSerializer,
                { TestValue(it) },
                { it.value },
            ),
        )
}
