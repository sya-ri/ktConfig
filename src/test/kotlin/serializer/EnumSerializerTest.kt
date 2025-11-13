package serializer

import dev.s7a.ktconfig.serializer.EnumSerializer
import kotlin.test.Test
import testSerializer

enum class TestEnum {
    VALUE1,
    VALUE2,
    VALUE3,
}

class EnumSerializerTest {
    @Test
    fun testValue1() = testSerializer(
        TestEnum.VALUE1,
        EnumSerializer(TestEnum::class.java),
    )

    @Test
    fun testValue2() = testSerializer(
        TestEnum.VALUE2,
        EnumSerializer(TestEnum::class.java),
    )

    @Test
    fun testValue3() = testSerializer(
        TestEnum.VALUE3,
        EnumSerializer(TestEnum::class.java),
    )
}

