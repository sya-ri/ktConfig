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
class UIntListTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: 123", listOf(123u)),
                GetTestData("value: [0]", listOf(0u)),
                GetTestData(
                    """
                    value:
                    - 456
                    """.trimIndent(),
                    listOf(456u),
                ),
                GetTestData("value: [789, 1011, 0]", listOf(789u, 1011u, 0u)),
                GetTestData(
                    """
                    value:
                    - 789
                    - 1011
                    - 0
                    """.trimIndent(),
                    listOf(789u, 1011u, 0u),
                ),
                GetTestData("value: []", listOf()),
                GetTestData("value: [4294967295, 2147483648]", listOf(UInt.MAX_VALUE, 2147483648u)),
                GetTestData("value: [15, 26]", listOf(15u, 26u)),
            ) { (yaml, value) ->
                ktConfigString<Data<List<UInt>>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<List<UInt>>>(yaml) shouldBe Data(value)
            }
        }
        context("should get null from config") {
            test("non-nullable list should throw TypeMismatchException for null value") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<List<UInt>>>("value: null")
                    }
                exception.message shouldBe "Expected kotlin.collections.List<kotlin.UInt>, but null: value"
            }
            test("non-nullable list element should throw TypeMismatchException for null element") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<List<UInt>>>("value: [null]")
                    }
                exception.message shouldBe "Expected kotlin.UInt, but null: value[0]"
            }
            test("nullable list should return Data(null) for null value") {
                ktConfigString<NullableData<List<UInt>>>("value: null") shouldBe Data(null)
            }
            test("nullable list element should return Data with null element") {
                ktConfigString<Data<List<UInt?>>>("value: [null]") shouldBe Data(listOf(null))
            }
        }
        context("should save value to config") {
            withData(
                SaveTestData(listOf<UInt>(), "value: []\n"),
                SaveTestData(
                    listOf(123u),
                    """
                    value:
                    - 123
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(789u, 1011u, 0u),
                    """
                    value:
                    - 789
                    - 1011
                    - 0
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf<UInt?>(null),
                    """
                    value:
                    - null
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(UInt.MAX_VALUE, 2147483648u),
                    """
                    value:
                    - 4294967295
                    - 2147483648
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(15u, 26u),
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
