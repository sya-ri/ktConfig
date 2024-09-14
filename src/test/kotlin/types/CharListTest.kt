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
class CharListTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: a", listOf('a')),
                GetTestData("value: [a]", listOf('a')),
                GetTestData(
                    """
                    value:
                    - a
                    """.trimIndent(),
                    listOf('a'),
                ),
                GetTestData("value: [a, '6', 9]", listOf('a', '6', 9.toChar())),
                GetTestData(
                    """
                    value:
                    - a
                    - '6'
                    - 9
                    - !!binary |-
                      Ag==
                    """.trimIndent(),
                    listOf('a', '6', 9.toChar(), 2.toChar()),
                ),
                GetTestData("value: []", listOf()),
                GetTestData("value: [' ', '@']", listOf(' ', '@')),
                GetTestData(
                    """
                    value:
                    - |2+
                    
                    - "\t"
                    
                    """.trimIndent(),
                    listOf('\n', '\t'),
                ),
            ) { (yaml, value) ->
                ktConfigString<Data<List<Char>>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<List<Char>>>(yaml) shouldBe Data(value)
            }
        }
        context("null handling in config") {
            test("non-nullable list should throw TypeMismatchException for null value") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<List<Char>>>("value: null")
                    }
                exception.message shouldBe "Expected kotlin.collections.List<kotlin.Char>, but null: value"
            }
            test("nullable list should return Data(null) for null value") {
                ktConfigString<NullableData<List<Char>>>("value: null") shouldBe Data(null)
            }
            test("non-nullable list element should throw TypeMismatchException for null element") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<List<Char>>>("value: [null]")
                    }
                exception.message shouldBe "Expected kotlin.Char, but null: value[0]"
            }
            test("nullable list element should return Data with null element") {
                ktConfigString<Data<List<Char?>>>("value: [null]") shouldBe Data(listOf(null))
            }
        }
        context("should save value to config") {
            withData(
                SaveTestData(listOf<Char>(), "value: []\n"),
                SaveTestData(
                    listOf('a'),
                    """
                    value:
                    - a
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf('a', '6', 9.toChar(), 2.toChar()),
                    """
                    value:
                    - a
                    - '6'
                    - "\t"
                    - !!binary |-
                      Ag==
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(' ', '@'),
                    """
                    value:
                    - ' '
                    - '@'
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf('\n', '\t'),
                    """
                    value:
                    - |2+
                    
                    - "\t"
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf<Char?>(null),
                    """
                    value:
                    - null
                    
                    """.trimIndent(),
                ),
            ) { (value, yaml) ->
                saveKtConfigString(Data(value)) shouldBe yaml
                saveKtConfigString(NullableData(value)) shouldBe yaml
            }
        }
    })
