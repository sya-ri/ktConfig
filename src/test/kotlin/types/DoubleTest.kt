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
class DoubleTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: 123.45", 123.45),
                GetTestData("value: 0.0", 0.0),
                GetTestData("value: -456.78", -456.78),
                GetTestData("value: '789.01'", 789.01),
                GetTestData("value: 1.0e2", 100.0),
                GetTestData("value: 3.14e-2", 0.0314),
                GetTestData("value: 1.7976931348623157E308", Double.MAX_VALUE),
                GetTestData("value: 4.9E-324", Double.MIN_VALUE),
                GetTestData("value: NaN", Double.NaN),
                GetTestData("value: Infinity", Double.POSITIVE_INFINITY),
                GetTestData("value: -Infinity", Double.NEGATIVE_INFINITY),
                GetTestData("value: .NaN", Double.NaN),
                GetTestData("value: .inf", Double.POSITIVE_INFINITY),
                GetTestData("value: -.inf", Double.NEGATIVE_INFINITY),
            ) { (yaml, value) ->
                ktConfigString<Data<Double>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<Double>>(yaml) shouldBe Data(value)
            }
        }

        context("should handle invalid config values") {
            withData(
                "value: 'not a number'",
                "value: '123abc'",
                "value: ''",
                "value: ' '",
            ) { yaml ->
                shouldThrow<TypeMismatchException> {
                    ktConfigString<Data<Double>>(yaml)
                }
            }
        }

        context("should get null from config") {
            test("non-nullable") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<Double>>("value: null")
                    }
                exception.message shouldBe "Expected kotlin.Double, but null: value"
            }
            test("nullable") {
                ktConfigString<NullableData<Double>>("value: null") shouldBe Data(null)
            }
        }

        context("should save value to config") {
            withData(
                SaveTestData(123.45, "value: 123.45\n"),
                SaveTestData(0.0, "value: 0.0\n"),
                SaveTestData(-456.78, "value: -456.78\n"),
                SaveTestData(789.01, "value: 789.01\n"),
                SaveTestData(100.0, "value: 100.0\n"),
                SaveTestData(0.0314, "value: 0.0314\n"),
                SaveTestData(Double.MAX_VALUE, "value: 1.7976931348623157E308\n"),
                SaveTestData(Double.MIN_VALUE, "value: 4.9E-324\n"),
                SaveTestData(Double.NaN, "value: .NaN\n"),
                SaveTestData(Double.POSITIVE_INFINITY, "value: .inf\n"),
                SaveTestData(Double.NEGATIVE_INFINITY, "value: -.inf\n"),
            ) { (value, yaml) ->
                saveKtConfigString(Data(value)) shouldBe yaml
                saveKtConfigString(NullableData(value)) shouldBe yaml
            }
        }

        context("should save null to config") {
            test("nullable") {
                saveKtConfigString(NullableData<Double>(null)) shouldBe ""
            }
        }
    })
