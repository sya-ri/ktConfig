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
class IntListTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: 123", listOf(123)),
                GetTestData("value: [0]", listOf(0)),
                GetTestData(
                    """
                    value:
                    - 456
                    """.trimIndent(),
                    listOf(456),
                ),
                GetTestData("value: [789, -1011, 0]", listOf(789, -1011, 0)),
                GetTestData(
                    """
                    value:
                    - 789
                    - -1011
                    - 0
                    """.trimIndent(),
                    listOf(789, -1011, 0),
                ),
                GetTestData("value: []", listOf()),
                GetTestData("value: [2147483647, -2147483648]", listOf(Int.MAX_VALUE, Int.MIN_VALUE)),
                GetTestData("value: [0b1111, 0x1A]", listOf(15, 26)),
            ) { (yaml, value) ->
                ktConfigString<Data<List<Int>>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<List<Int>>>(yaml) shouldBe Data(value)
            }
        }
        context("should get null from config") {
            test("non-nullable list should throw TypeMismatchException for null value") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<List<Int>>>("value: null")
                    }
                exception.message shouldBe "Expected kotlin.collections.List<kotlin.Int>, but null: value"
            }
            test("non-nullable list element should throw TypeMismatchException for null element") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<List<Int>>>("value: [null]")
                    }
                exception.message shouldBe "Expected kotlin.Int, but null: value[0]"
            }
            test("nullable list should return Data(null) for null value") {
                ktConfigString<NullableData<List<Int>>>("value: null") shouldBe Data(null)
            }
            test("nullable list element should return Data with null element") {
                ktConfigString<Data<List<Int?>>>("value: [null]") shouldBe Data(listOf(null))
            }
        }
        context("should save value to config") {
            withData(
                SaveTestData(listOf<Int>(), "value: []\n"),
                SaveTestData(
                    listOf(123),
                    """
                    value:
                    - 123
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(789, -1011, 0),
                    """
                    value:
                    - 789
                    - -1011
                    - 0
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf<Int?>(null),
                    """
                    value:
                    - null
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(Int.MAX_VALUE, Int.MIN_VALUE),
                    """
                    value:
                    - 2147483647
                    - -2147483648
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(15, 26),
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
