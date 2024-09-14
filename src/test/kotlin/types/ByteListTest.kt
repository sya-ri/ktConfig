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
class ByteListTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: 123", listOf(123.toByte())),
                GetTestData("value: [0]", listOf(0.toByte())),
                GetTestData(
                    """
                    value:
                    - 56
                    """.trimIndent(),
                    listOf(56.toByte()),
                ),
                GetTestData("value: [89, -123, 0]", listOf(89.toByte(), (-123).toByte(), 0.toByte())),
                GetTestData(
                    """
                    value:
                    - 89
                    - -123
                    - 0
                    """.trimIndent(),
                    listOf(89.toByte(), (-123).toByte(), 0.toByte()),
                ),
                GetTestData("value: []", listOf()),
                GetTestData("value: [127, -128]", listOf(Byte.MAX_VALUE, Byte.MIN_VALUE)),
                GetTestData("value: [15, 26]", listOf(15.toByte(), 26.toByte())),
            ) { (yaml, value) ->
                ktConfigString<Data<List<Byte>>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<List<Byte>>>(yaml) shouldBe Data(value)
            }
        }
        context("should get null from config") {
            test("non-nullable list should throw TypeMismatchException for null value") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<List<Byte>>>("value: null")
                    }
                exception.message shouldBe "Expected kotlin.collections.List<kotlin.Byte>, but null: value"
            }
            test("non-nullable list element should throw TypeMismatchException for null element") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<List<Byte>>>("value: [null]")
                    }
                exception.message shouldBe "Expected kotlin.Byte, but null: value[0]"
            }
            test("nullable list should return Data(null) for null value") {
                ktConfigString<NullableData<List<Byte>>>("value: null") shouldBe Data(null)
            }
            test("nullable list element should return Data with null element") {
                ktConfigString<Data<List<Byte?>>>("value: [null]") shouldBe Data(listOf(null))
            }
        }
        context("should save value to config") {
            withData(
                SaveTestData(listOf<Byte>(), "value: []\n"),
                SaveTestData(
                    listOf(123.toByte()),
                    """
                    value:
                    - 123
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(89.toByte(), (-123).toByte(), 0.toByte()),
                    """
                    value:
                    - 89
                    - -123
                    - 0
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf<Byte?>(null),
                    """
                    value:
                    - null
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(Byte.MAX_VALUE, Byte.MIN_VALUE),
                    """
                    value:
                    - 127
                    - -128
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(15.toByte(), 26.toByte()),
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
