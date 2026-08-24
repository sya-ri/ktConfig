import dev.s7a.ktconfig.KtConfig
import dev.s7a.ktconfig.SerialName
import kotlin.test.Test
import kotlin.test.assertEquals

@KtConfig
sealed interface SealedConfig {
    @KtConfig
    data class A(
        val a: String,
        val value: Int,
        val enum: Enum,
    ) : SealedConfig {
        enum class Enum {
            TestA,
        }
    }

    @KtConfig(discriminator = "type")
    sealed interface B : SealedConfig {
        @KtConfig
        @SerialName("b1")
        data class B1(
            val b1: String,
            val enum: Enum,
        ) : B {
            enum class Enum {
                TestB1,
            }
        }

        @KtConfig
        data class B2(
            val b2: String,
        ) : B
    }
}

class SealedKtConfigTest {
    @Test
    fun testLoadsRootSubtypeByQualifiedNameDiscriminator() {
        val expected = SealedConfig.A("text1", 5, SealedConfig.A.Enum.TestA)

        assertEquals(expected, SealedConfigLoader.loadFromString(rootSubtypeYaml()))
    }

    @Test
    fun testSavesRootSubtypeWithQualifiedNameDiscriminator() {
        val value = SealedConfig.A("text1", 5, SealedConfig.A.Enum.TestA)

        assertEquals(rootSubtypeYaml(), SealedConfigLoader.saveToString(value))
    }

    @Test
    fun testLoadsNestedSubtypeBySerialNameDiscriminator() {
        val expected = SealedConfig.B.B1("text2", SealedConfig.B.B1.Enum.TestB1)

        assertEquals(expected, SealedConfigLoader.loadFromString(serialNameSubtypeYaml()))
    }

    @Test
    fun testSavesNestedSubtypeWithSerialNameDiscriminator() {
        val value = SealedConfig.B.B1("text2", SealedConfig.B.B1.Enum.TestB1)

        assertEquals(serialNameSubtypeYaml(), SealedConfigLoader.saveToString(value))
    }

    @Test
    fun testLoadsNestedSubtypeByQualifiedNameDiscriminator() {
        val expected = SealedConfig.B.B2("text3")

        assertEquals(expected, SealedConfigLoader.loadFromString(nestedSubtypeYaml()))
    }

    @Test
    fun testSavesNestedSubtypeWithQualifiedNameDiscriminator() {
        val value = SealedConfig.B.B2("text3")

        assertEquals(nestedSubtypeYaml(), SealedConfigLoader.saveToString(value))
    }

    @Test
    fun testLoadsNestedSubtypeWithCustomDiscriminator() {
        val expected = SealedConfig.B.B1("text2", SealedConfig.B.B1.Enum.TestB1)

        assertEquals(expected, SealedConfigBLoader.loadFromString(customDiscriminatorYaml()))
    }

    @Test
    fun testSavesNestedSubtypeWithCustomDiscriminator() {
        val value = SealedConfig.B.B1("text2", SealedConfig.B.B1.Enum.TestB1)

        assertEquals(customDiscriminatorYaml(), SealedConfigBLoader.saveToString(value))
    }

    private fun rootSubtypeYaml() =
        """
        $: SealedConfig.A
        a: text1
        value: '5'
        enum: TestA
        
        """.trimIndent()

    private fun serialNameSubtypeYaml() =
        """
        $: b1
        b1: text2
        enum: TestB1
        
        """.trimIndent()

    private fun nestedSubtypeYaml() =
        """
        $: SealedConfig.B.B2
        b2: text3
        
        """.trimIndent()

    private fun customDiscriminatorYaml() =
        """
        type: b1
        b1: text2
        enum: TestB1
        
        """.trimIndent()
}
