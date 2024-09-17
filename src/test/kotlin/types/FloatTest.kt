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
class FloatTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: 123.45", 123.45f),
                GetTestData("value: 0.0", 0.0f),
                GetTestData("value: -456.78", -456.78f),
                GetTestData("value: '789.01'", 789.01f),
                GetTestData("value: 1.0e2", 100.0f),
                GetTestData("value: 3.14e-2", 0.0314f),
                GetTestData("value: 3.4028235E38", Float.MAX_VALUE),
                GetTestData("value: 1.4E-45", Float.MIN_VALUE),
                GetTestData("value: NaN", Float.NaN),
                GetTestData("value: Infinity", Float.POSITIVE_INFINITY),
                GetTestData("value: -Infinity", Float.NEGATIVE_INFINITY),
                GetTestData("value: .NaN", Float.NaN),
                GetTestData("value: .inf", Float.POSITIVE_INFINITY),
                GetTestData("value: -.inf", Float.NEGATIVE_INFINITY),
                GetTestData("value: !!float 123.45", 123.45f),
                GetTestData("value: !!float 0.0", 0.0f),
                GetTestData("value: !!float -456.78", -456.78f),
                GetTestData("value: !!float '789.01'", 789.01f),
                GetTestData("value: !!float 1.0e2", 100.0f),
                GetTestData("value: !!float 3.14e-2", 0.0314f),
                GetTestData("value: !!float 3.4028235E38", Float.MAX_VALUE),
                GetTestData("value: !!float 1.4E-45", Float.MIN_VALUE),
                GetTestData("value: !!float 'NaN'", Float.NaN),
                GetTestData("value: !!float 'Infinity'", Float.POSITIVE_INFINITY),
                GetTestData("value: !!float '-Infinity'", Float.NEGATIVE_INFINITY),
            ) { (yaml, value) ->
                ktConfigString<Data<Float>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<Float>>(yaml) shouldBe Data(value)
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
                    ktConfigString<Data<Float>>(yaml)
                }
            }
        }
        context("should get null from config") {
            test("non-nullable") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<Float>>("value: null")
                    }
                exception.message shouldBe "Expected kotlin.Float, but null: value"
            }
            test("nullable") {
                ktConfigString<NullableData<Float>>("value: null") shouldBe Data(null)
            }
        }
        context("should save value to config") {
            withData(
                SaveTestData(123.45f, "value: 123.45\n"),
                SaveTestData(0.0f, "value: 0.0\n"),
                SaveTestData(-456.78f, "value: -456.78\n"),
                SaveTestData(789.01f, "value: 789.01\n"),
                SaveTestData(100.0f, "value: 100.0\n"),
                SaveTestData(0.0314f, "value: 0.0314\n"),
                SaveTestData(Float.MAX_VALUE, "value: 3.4028235E38\n"),
                SaveTestData(Float.MIN_VALUE, "value: 1.4E-45\n"),
                SaveTestData(Float.NaN, "value: !!float 'NaN'\n"),
                SaveTestData(Float.POSITIVE_INFINITY, "value: !!float 'Infinity'\n"),
                SaveTestData(Float.NEGATIVE_INFINITY, "value: !!float '-Infinity'\n"),
            ) { (value, yaml) ->
                saveKtConfigString(Data(value)) shouldBe yaml
                saveKtConfigString(NullableData(value)) shouldBe yaml
            }
        }
        context("should save null to config") {
            test("nullable") {
                saveKtConfigString(NullableData<Float>(null)) shouldBe ""
            }
        }
    })
