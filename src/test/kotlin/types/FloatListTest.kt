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
class FloatListTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: [123.45]", listOf(123.45f)),
                GetTestData("value: [0.0]", listOf(0.0f)),
                GetTestData(
                    """
                    value:
                    - -456.78
                    """.trimIndent(),
                    listOf(-456.78f),
                ),
                GetTestData("value: [789.01, -123.45, 0.0]", listOf(789.01f, -123.45f, 0.0f)),
                GetTestData(
                    """
                    value:
                    - 789.01
                    - -123.45
                    - 0.0
                    """.trimIndent(),
                    listOf(789.01f, -123.45f, 0.0f),
                ),
                GetTestData("value: []", listOf()),
                GetTestData("value: [3.4028235E38, 1.4E-45]", listOf(Float.MAX_VALUE, Float.MIN_VALUE)),
                GetTestData("value: [NaN, Infinity, -Infinity]", listOf(Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY)),
                GetTestData(
                    "value: [!!float 'NaN', !!float 'Infinity', !!float '-Infinity']",
                    listOf(Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY),
                ),
            ) { (yaml, value) ->
                ktConfigString<Data<List<Float>>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<List<Float>>>(yaml) shouldBe Data(value)
            }
        }
        context("should get null from config") {
            test("non-nullable list should throw TypeMismatchException for null value") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<List<Float>>>("value: null")
                    }
                exception.message shouldBe "Expected kotlin.collections.List<kotlin.Float>, but null: value"
            }
            test("non-nullable list element should throw TypeMismatchException for null element") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<List<Float>>>("value: [null]")
                    }
                exception.message shouldBe "Expected kotlin.Float, but null: value[0]"
            }
            test("nullable list should return Data(null) for null value") {
                ktConfigString<NullableData<List<Float>>>("value: null") shouldBe Data(null)
            }
            test("nullable list element should return Data with null element") {
                ktConfigString<Data<List<Float?>>>("value: [null]") shouldBe Data(listOf(null))
            }
        }
        context("should save value to config") {
            withData(
                SaveTestData(listOf<Float>(), "value: []\n"),
                SaveTestData(
                    listOf(123.45f),
                    """
                    value:
                    - 123.45
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(789.01f, -123.45f, 0.0f),
                    """
                    value:
                    - 789.01
                    - -123.45
                    - 0.0
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf<Float?>(null),
                    """
                    value:
                    - null
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(Float.MAX_VALUE, Float.MIN_VALUE),
                    """
                    value:
                    - 3.4028235E38
                    - 1.4E-45
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY),
                    """
                    value:
                    - !!float 'NaN'
                    - !!float 'Infinity'
                    - !!float '-Infinity'
                    
                    """.trimIndent(),
                ),
            ) { (value, yaml) ->
                saveKtConfigString(Data(value)) shouldBe yaml
                saveKtConfigString(NullableData(value)) shouldBe yaml
            }
        }
    })
