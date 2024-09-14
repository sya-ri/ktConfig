package types
import dev.s7a.ktconfig.exception.TypeMismatchException
import dev.s7a.ktconfig.ktConfigString
import dev.s7a.ktconfig.saveKtConfigString
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import utils.Data
import utils.GetTestData
import utils.NullableData
import utils.SaveTestData

@Suppress("unused")
class LongListTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: 123", listOf(123L)),
                GetTestData("value: [0]", listOf(0L)),
                GetTestData(
                    """
                    value:
                    - 456
                    """.trimIndent(),
                    listOf(456L),
                ),
                GetTestData("value: [789, -1011, 0]", listOf(789L, -1011L, 0L)),
                GetTestData(
                    """
                    value:
                    - 789
                    - -1011
                    - 0
                    """.trimIndent(),
                    listOf(789L, -1011L, 0L),
                ),
                GetTestData("value: []", listOf()),
                GetTestData("value: [9223372036854775807, -9223372036854775808]", listOf(Long.MAX_VALUE, Long.MIN_VALUE)),
                GetTestData("value: [0b1111, 0x1A]", listOf(15L, 26L)),
            ) { (yaml, value) ->
                ktConfigString<Data<List<Long>>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<List<Long>>>(yaml) shouldBe Data(value)
            }
        }
        context("should get null from config") {
            test("non-nullable list should throw TypeMismatchException for null value") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<List<Long>>>("value: null")
                    }
                exception.message shouldBe "Expected kotlin.collections.List<kotlin.Long>, but null: value"
            }
            test("non-nullable list element should throw TypeMismatchException for null element") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<List<Long>>>("value: [null]")
                    }
                exception.message shouldBe "Expected kotlin.Long, but null: value[0]"
            }
            test("nullable list should return Data(null) for null value") {
                ktConfigString<NullableData<List<Long>>>("value: null") shouldBe Data(null)
            }
            test("nullable list element should return Data with null element") {
                ktConfigString<Data<List<Long?>>>("value: [null]") shouldBe Data(listOf(null))
            }
        }
        context("should save value to config") {
            withData(
                SaveTestData(listOf<Long>(), "value: []\n"),
                SaveTestData(
                    listOf(123L),
                    """
                    value:
                    - 123
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(789L, -1011L, 0L),
                    """
                    value:
                    - 789
                    - -1011
                    - 0
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf<Long?>(null),
                    """
                    value:
                    - null
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(Long.MAX_VALUE, Long.MIN_VALUE),
                    """
                    value:
                    - 9223372036854775807
                    - -9223372036854775808
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(15L, 26L),
                    """
                    value:
                    - 15
                    - 26
                    
                    """.trimIndent(),
                ),
            ) { (value, yaml) ->
                saveKtConfigString(Data(value)) shouldBe yaml
                saveKtConfigString(NullableData(value)) shouldBe yaml
            }
        }
    })
