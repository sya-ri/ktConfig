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
class DoubleListTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: [123.45]", listOf(123.45)),
                GetTestData("value: [0.0]", listOf(0.0)),
                GetTestData(
                    """
                    value:
                    - -456.78
                    """.trimIndent(),
                    listOf(-456.78),
                ),
                GetTestData("value: [789.01, -123.45, 0.0]", listOf(789.01, -123.45, 0.0)),
                GetTestData(
                    """
                    value:
                    - 789.01
                    - -123.45
                    - 0.0
                    """.trimIndent(),
                    listOf(789.01, -123.45, 0.0),
                ),
                GetTestData("value: []", listOf()),
                GetTestData("value: [1.7976931348623157E308, 4.9E-324]", listOf(Double.MAX_VALUE, Double.MIN_VALUE)),
                GetTestData("value: [NaN, Infinity, -Infinity]", listOf(Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY)),
                GetTestData(
                    "value: [!!float 'NaN', !!float 'Infinity', !!float '-Infinity']",
                    listOf(Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY),
                ),
            ) { (yaml, value) ->
                ktConfigString<Data<List<Double>>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<List<Double>>>(yaml) shouldBe Data(value)
            }
        }
        context("should get null from config") {
            test("non-nullable list should throw TypeMismatchException for null value") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<List<Double>>>("value: null")
                    }
                exception.message shouldBe "Expected kotlin.collections.List<kotlin.Double>, but null: value"
            }
            test("non-nullable list element should throw TypeMismatchException for null element") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<List<Double>>>("value: [null]")
                    }
                exception.message shouldBe "Expected kotlin.Double, but null: value[0]"
            }
            test("nullable list should return Data(null) for null value") {
                ktConfigString<NullableData<List<Double>>>("value: null") shouldBe Data(null)
            }
            test("nullable list element should return Data with null element") {
                ktConfigString<Data<List<Double?>>>("value: [null]") shouldBe Data(listOf(null))
            }
        }
        context("should save value to config") {
            withData(
                SaveTestData(listOf<Double>(), "value: []\n"),
                SaveTestData(
                    listOf(123.45),
                    """
                    value:
                    - 123.45
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(789.01, -123.45, 0.0),
                    """
                    value:
                    - 789.01
                    - -123.45
                    - 0.0
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf<Double?>(null),
                    """
                    value:
                    - null
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(Double.MAX_VALUE, Double.MIN_VALUE),
                    """
                    value:
                    - 1.7976931348623157E308
                    - 4.9E-324
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf(Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY),
                    """
                    value:
                    - .NaN
                    - .inf
                    - -.inf
                    
                    """.trimIndent(),
                ),
            ) { (value, yaml) ->
                saveKtConfigString(Data(value)) shouldBe yaml
                saveKtConfigString(NullableData(value)) shouldBe yaml
            }
        }
    })
