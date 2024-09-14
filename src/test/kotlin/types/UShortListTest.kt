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
class UShortListTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: 123", listOf(123.toUShort())),
                GetTestData("value: [0]", listOf(0.toUShort())),
                GetTestData(
                    """
                    value:
                    - 456
                    """.trimIndent(),
                    listOf(456.toUShort()),
                ),
                GetTestData("value: [789, 1011, 0]", listOf(789.toUShort(), 1011.toUShort(), 0.toUShort())),
                GetTestData(
                    """
                    value:
                    - 789
                    - 1011
                    - 0
                    """.trimIndent(),
                    listOf(789.toUShort(), 1011.toUShort(), 0.toUShort()),
                ),
                GetTestData("value: []", listOf()),
                GetTestData("value: [65535, 32768]", listOf(UShort.MAX_VALUE, 32768.toUShort())),
                GetTestData("value: [15, 26]", listOf(15.toUShort(), 26.toUShort())),
            ) { (yaml, value) ->
                ktConfigString<Data<List<UShort>>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<List<UShort>>>(yaml) shouldBe Data(value)
            }
        }
        context("should get null from config") {
            test("non-nullable list should throw TypeMismatchException for null value") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<List<UShort>>>("value: null")
                    }
                exception.message shouldBe "Expected kotlin.collections.List<kotlin.UShort>, but null: value"
            }
            test("non-nullable list element should throw TypeMismatchException for null element") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<List<UShort>>>("value: [null]")
                    }
                exception.message shouldBe "Expected kotlin.UShort, but null: value[0]"
            }
            test("nullable list should return Data(null) for null value") {
                ktConfigString<NullableData<List<UShort>>>("value: null") shouldBe Data(null)
            }
            test("nullable list element should return Data with null element") {
                ktConfigString<Data<List<UShort?>>>("value: [null]") shouldBe Data(listOf(null))
            }
        }
        context("should save value to config") {
            withData(
                SaveTestData(listOf<UShort>(), "value: []\n"),
                SaveTestData(
                    listOf(123.toUShort()),
                    """
                    value:
                    - 123
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(789.toUShort(), 1011.toUShort(), 0.toUShort()),
                    """
                    value:
                    - 789
                    - 1011
                    - 0
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf<UShort?>(null),
                    """
                    value:
                    - null
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(UShort.MAX_VALUE, 32768.toUShort()),
                    """
                    value:
                    - 65535
                    - 32768
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(15.toUShort(), 26.toUShort()),
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
