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
class UShortTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: 123", 123.toUShort()),
                GetTestData("value: 0", 0.toUShort()),
                GetTestData("value: 65535", UShort.MAX_VALUE),
                GetTestData("value: 1", 1.toUShort()),
                GetTestData("value: 32768", 32768.toUShort()),
                GetTestData("value: 65536", 0.toUShort()), // Overflow
                GetTestData("value: -1", UShort.MAX_VALUE), // Underflow
            ) { (yaml, value) ->
                ktConfigString<Data<UShort>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<UShort>>(yaml) shouldBe Data(value)
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
                    ktConfigString<Data<UShort>>(yaml)
                }
            }
        }
        context("should get null from config") {
            test("non-nullable") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<UShort>>("value: null")
                    }
                exception.message shouldBe "Expected kotlin.UShort, but null: value"
            }
            test("nullable") {
                ktConfigString<NullableData<UShort>>("value: null") shouldBe Data(null)
            }
        }
        context("should save value to config") {
            withData(
                SaveTestData(123.toUShort(), "value: 123\n"),
                SaveTestData(0.toUShort(), "value: 0\n"),
                SaveTestData(UShort.MAX_VALUE, "value: 65535\n"),
                SaveTestData(1.toUShort(), "value: 1\n"),
                SaveTestData(32768.toUShort(), "value: 32768\n"),
            ) { (value, yaml) ->
                saveKtConfigString(Data(value)) shouldBe yaml
                saveKtConfigString(NullableData(value)) shouldBe yaml
            }
        }
        context("should save null to config") {
            test("nullable") {
                saveKtConfigString(NullableData<UShort>(null)) shouldBe ""
            }
        }
    })
