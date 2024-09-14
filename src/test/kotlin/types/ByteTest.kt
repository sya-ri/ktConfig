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
class ByteTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: 123", 123.toByte()),
                GetTestData("value: 0", 0.toByte()),
                GetTestData("value: -56", (-56).toByte()),
                GetTestData("value: '89'", 89.toByte()),
                GetTestData("value: 15", 15.toByte()),
                GetTestData("value: 26", 26.toByte()),
                GetTestData("value: 127", Byte.MAX_VALUE),
                GetTestData("value: -128", Byte.MIN_VALUE),
                GetTestData("value: 128", Byte.MIN_VALUE), // Overflow
                GetTestData("value: -129", Byte.MAX_VALUE), // Underflow
                GetTestData("value: 1", 1.toByte()),
                GetTestData("value: -1", (-1).toByte()),
            ) { (yaml, value) ->
                ktConfigString<Data<Byte>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<Byte>>(yaml) shouldBe Data(value)
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
                    ktConfigString<Data<Byte>>(yaml)
                }
            }
        }

        context("should get null from config") {
            test("non-nullable") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<Byte>>("value: null")
                    }
                exception.message shouldBe "Expected kotlin.Byte, but null: value"
            }
            test("nullable") {
                ktConfigString<NullableData<Byte>>("value: null") shouldBe Data(null)
            }
        }

        context("should save value to config") {
            withData(
                SaveTestData(123.toByte(), "value: 123\n"),
                SaveTestData(0.toByte(), "value: 0\n"),
                SaveTestData((-56).toByte(), "value: -56\n"),
                SaveTestData(89.toByte(), "value: 89\n"),
                SaveTestData(15.toByte(), "value: 15\n"),
                SaveTestData(26.toByte(), "value: 26\n"),
                SaveTestData(Byte.MAX_VALUE, "value: 127\n"),
                SaveTestData(Byte.MIN_VALUE, "value: -128\n"),
                SaveTestData(1.toByte(), "value: 1\n"),
                SaveTestData((-1).toByte(), "value: -1\n"),
            ) { (value, yaml) ->
                saveKtConfigString(Data(value)) shouldBe yaml
                saveKtConfigString(NullableData(value)) shouldBe yaml
            }
        }

        context("should save null to config") {
            test("nullable") {
                saveKtConfigString(NullableData<Byte>(null)) shouldBe ""
            }
        }
    })
