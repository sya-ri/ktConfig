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
class ShortListTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: 123", listOf(123.toShort())),
                GetTestData("value: [0]", listOf(0.toShort())),
                GetTestData(
                    """
                    value:
                    - 456
                    """.trimIndent(),
                    listOf(456.toShort()),
                ),
                GetTestData("value: [789, -1011, 0]", listOf(789.toShort(), (-1011).toShort(), 0.toShort())),
                GetTestData(
                    """
                    value:
                    - 789
                    - -1011
                    - 0
                    """.trimIndent(),
                    listOf(789.toShort(), (-1011).toShort(), 0.toShort()),
                ),
                GetTestData("value: []", listOf()),
                GetTestData("value: [32767, -32768]", listOf(Short.MAX_VALUE, Short.MIN_VALUE)),
                GetTestData("value: [15, 26]", listOf(15.toShort(), 26.toShort())),
            ) { (yaml, value) ->
                ktConfigString<Data<List<Short>>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<List<Short>>>(yaml) shouldBe Data(value)
            }
        }
        context("should get null from config") {
            test("non-nullable list should throw TypeMismatchException for null value") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<List<Short>>>("value: null")
                    }
                exception.message shouldBe "Expected kotlin.collections.List<kotlin.Short>, but null: value"
            }
            test("non-nullable list element should throw TypeMismatchException for null element") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<List<Short>>>("value: [null]")
                    }
                exception.message shouldBe "Expected kotlin.Short, but null: value[0]"
            }
            test("nullable list should return Data(null) for null value") {
                ktConfigString<NullableData<List<Short>>>("value: null") shouldBe Data(null)
            }
            test("nullable list element should return Data with null element") {
                ktConfigString<Data<List<Short?>>>("value: [null]") shouldBe Data(listOf(null))
            }
        }
        context("should save value to config") {
            withData(
                SaveTestData(listOf<Short>(), "value: []\n"),
                SaveTestData(
                    listOf(123.toShort()),
                    """
                    value:
                    - 123
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(789.toShort(), (-1011).toShort(), 0.toShort()),
                    """
                    value:
                    - 789
                    - -1011
                    - 0
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf<Short?>(null),
                    """
                    value:
                    - null
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(Short.MAX_VALUE, Short.MIN_VALUE),
                    """
                    value:
                    - 32767
                    - -32768
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(15.toShort(), 26.toShort()),
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
