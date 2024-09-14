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
class StringListTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: config string", listOf("config string")),
                GetTestData("value: [config string]", listOf("config string")),
                GetTestData(
                    """
                    value:
                    - config string
                    """.trimIndent(),
                    listOf("config string"),
                ),
                GetTestData("value: [config string, other string]", listOf("config string", "other string")),
                GetTestData(
                    """
                    value:
                    - config string
                    - other string
                    """.trimIndent(),
                    listOf("config string", "other string"),
                ),
                GetTestData("value: []", listOf()),
                GetTestData("value: ['特殊文字!@#\$%^&*()']", listOf("特殊文字!@#\$%^&*()")),
                GetTestData("value: ['123abc']", listOf("123abc")),
                GetTestData(
                    """
                    value:
                    - line1
                    - line2
                    """.trimIndent(),
                    listOf("line1", "line2"),
                ),
            ) { (yaml, value) ->
                ktConfigString<Data<List<String>>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<List<String>>>(yaml) shouldBe Data(value)
            }
        }
        context("should get null from config") {
            test("non-nullable list should throw TypeMismatchException for null value") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<List<String>>>("value: null")
                    }
                exception.message shouldBe "Expected kotlin.collections.List<kotlin.String>, but null: value"
            }
            test("non-nullable list element should throw TypeMismatchException for null element") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<List<String>>>("value: [null]")
                    }
                exception.message shouldBe "Expected kotlin.String, but null: value[0]"
            }
            test("nullable list should return Data(null) for null value") {
                ktConfigString<NullableData<List<String>>>("value: null") shouldBe Data(null)
            }
            test("nullable list element should return Data with null element") {
                ktConfigString<Data<List<String?>>>("value: [null]") shouldBe Data(listOf(null))
            }
        }
        context("should save value to config") {
            withData(
                SaveTestData(listOf<String>(), "value: []\n"),
                SaveTestData(
                    listOf("config string"),
                    """
                    value:
                    - config string
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf("config string", "other string"),
                    """
                    value:
                    - config string
                    - other string
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf<String?>(null),
                    """
                    value:
                    - null
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf("特殊文字!@#\$%^&*()"),
                    """
                    value:
                    - 特殊文字!@#$%^&*()
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf("123abc"),
                    """
                    value:
                    - 123abc
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf("line1\nline2"),
                    """
                    value:
                    - |-
                      line1
                      line2
                    
                    """.trimIndent(),
                ),
            ) { (value, yaml) ->
                saveKtConfigString(Data(value)) shouldBe yaml
                saveKtConfigString(NullableData(value)) shouldBe yaml
            }
        }
    })
