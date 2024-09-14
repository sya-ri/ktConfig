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
class UByteListTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: 123", listOf(123.toUByte())),
                GetTestData("value: [0]", listOf(0.toUByte())),
                GetTestData(
                    """
                    value:
                    - 56
                    """.trimIndent(),
                    listOf(56.toUByte()),
                ),
                GetTestData("value: [89, 234, 0]", listOf(89.toUByte(), 234.toUByte(), 0.toUByte())),
                GetTestData(
                    """
                    value:
                    - 89
                    - 234
                    - 0
                    """.trimIndent(),
                    listOf(89.toUByte(), 234.toUByte(), 0.toUByte()),
                ),
                GetTestData("value: []", listOf()),
                GetTestData("value: [255, 128]", listOf(UByte.MAX_VALUE, 128.toUByte())),
                GetTestData("value: [15, 26]", listOf(15.toUByte(), 26.toUByte())),
            ) { (yaml, value) ->
                ktConfigString<Data<List<UByte>>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<List<UByte>>>(yaml) shouldBe Data(value)
            }
        }
        context("should get null from config") {
            test("non-nullable list should throw TypeMismatchException for null value") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<List<UByte>>>("value: null")
                    }
                exception.message shouldBe "Expected kotlin.collections.List<kotlin.UByte>, but null: value"
            }
            test("non-nullable list element should throw TypeMismatchException for null element") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<List<UByte>>>("value: [null]")
                    }
                exception.message shouldBe "Expected kotlin.UByte, but null: value[0]"
            }
            test("nullable list should return Data(null) for null value") {
                ktConfigString<NullableData<List<UByte>>>("value: null") shouldBe Data(null)
            }
            test("nullable list element should return Data with null element") {
                ktConfigString<Data<List<UByte?>>>("value: [null]") shouldBe Data(listOf(null))
            }
        }
        context("should save value to config") {
            withData(
                SaveTestData(listOf<UByte>(), "value: []\n"),
                SaveTestData(
                    listOf(123.toUByte()),
                    """
                    value:
                    - 123
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(89.toUByte(), 234.toUByte(), 0.toUByte()),
                    """
                    value:
                    - 89
                    - 234
                    - 0
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf<UByte?>(null),
                    """
                    value:
                    - null
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(UByte.MAX_VALUE, 128.toUByte()),
                    """
                    value:
                    - 255
                    - 128
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(15.toUByte(), 26.toUByte()),
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
