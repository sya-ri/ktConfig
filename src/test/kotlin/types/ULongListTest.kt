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
class ULongListTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: 123", listOf(123uL)),
                GetTestData("value: [0]", listOf(0uL)),
                GetTestData(
                    """
                    value:
                    - 456
                    """.trimIndent(),
                    listOf(456uL),
                ),
                GetTestData("value: [789, 1011, 0]", listOf(789uL, 1011uL, 0uL)),
                GetTestData(
                    """
                    value:
                    - 789
                    - 1011
                    - 0
                    """.trimIndent(),
                    listOf(789uL, 1011uL, 0uL),
                ),
                GetTestData("value: []", listOf()),
                GetTestData("value: [18446744073709551615, 9223372036854775808]", listOf(ULong.MAX_VALUE, 9223372036854775808uL)),
                GetTestData("value: [15, 26]", listOf(15uL, 26uL)),
            ) { (yaml, value) ->
                ktConfigString<Data<List<ULong>>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<List<ULong>>>(yaml) shouldBe Data(value)
            }
        }
        context("should get null from config") {
            test("non-nullable list should throw TypeMismatchException for null value") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<List<ULong>>>("value: null")
                    }
                exception.message shouldBe "Expected kotlin.collections.List<kotlin.ULong>, but null: value"
            }
            test("non-nullable list element should throw TypeMismatchException for null element") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<List<ULong>>>("value: [null]")
                    }
                exception.message shouldBe "Expected kotlin.ULong, but null: value[0]"
            }
            test("nullable list should return Data(null) for null value") {
                ktConfigString<NullableData<List<ULong>>>("value: null") shouldBe Data(null)
            }
            test("nullable list element should return Data with null element") {
                ktConfigString<Data<List<ULong?>>>("value: [null]") shouldBe Data(listOf(null))
            }
        }
        context("should save value to config") {
            withData(
                SaveTestData(listOf<ULong>(), "value: []\n"),
                SaveTestData(
                    listOf(123uL),
                    """
                    value:
                    - 123
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(789uL, 1011uL, 0uL),
                    """
                    value:
                    - 789
                    - 1011
                    - 0
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf<ULong?>(null),
                    """
                    value:
                    - null
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(ULong.MAX_VALUE, 9223372036854775808uL),
                    """
                    value:
                    - 18446744073709551615
                    - 9223372036854775808
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(15uL, 26uL),
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
