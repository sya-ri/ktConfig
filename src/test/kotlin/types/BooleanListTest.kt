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
class BooleanListTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: true", listOf(true)),
                GetTestData("value: false", listOf(false)),
                GetTestData(
                    """
                    value:
                    - true
                    """.trimIndent(),
                    listOf(true),
                ),
                GetTestData("value: [true, false, true]", listOf(true, false, true)),
                GetTestData(
                    """
                    value:
                    - true
                    - false
                    - true
                    """.trimIndent(),
                    listOf(true, false, true),
                ),
                GetTestData("value: []", listOf()),
                GetTestData("value: ['true', 'false']", listOf(true, false)),
                GetTestData("value: [yes, no]", listOf(true, false)),
            ) { (yaml, value) ->
                ktConfigString<Data<List<Boolean>>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<List<Boolean>>>(yaml) shouldBe Data(value)
            }
        }
        context("should get null from config") {
            test("non-nullable list should throw TypeMismatchException for null value") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<List<Boolean>>>("value: null")
                    }
                exception.message shouldBe "Expected kotlin.collections.List<kotlin.Boolean>, but null: value"
            }
            test("non-nullable list element should throw TypeMismatchException for null element") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<List<Boolean>>>("value: [null]")
                    }
                exception.message shouldBe "Expected kotlin.Boolean, but null: value[0]"
            }
            test("nullable list should return Data(null) for null value") {
                ktConfigString<NullableData<List<Boolean>>>("value: null") shouldBe Data(null)
            }
            test("nullable list element should return Data with null element") {
                ktConfigString<Data<List<Boolean?>>>("value: [null]") shouldBe Data(listOf(null))
            }
        }
        context("should save value to config") {
            withData(
                SaveTestData(listOf<Boolean>(), "value: []\n"),
                SaveTestData(
                    listOf(true),
                    """
                    value:
                    - true
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(true, false, true),
                    """
                    value:
                    - true
                    - false
                    - true
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf<Boolean?>(null),
                    """
                    value:
                    - null
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(true, false),
                    """
                    value:
                    - true
                    - false
                    
                    """.trimIndent(),
                ),
            ) { (value, yaml) ->
                saveKtConfigString(Data(value)) shouldBe yaml
                saveKtConfigString(NullableData(value)) shouldBe yaml
            }
        }
    })
