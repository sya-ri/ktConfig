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
class ShortTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: 123", 123.toShort()),
                GetTestData("value: 0", 0.toShort()),
                GetTestData("value: -456", (-456).toShort()),
                GetTestData("value: '789'", 789.toShort()),
                GetTestData("value: 0b1111", 15.toShort()),
                GetTestData("value: 0x1A", 26.toShort()),
                GetTestData("value: 32767", Short.MAX_VALUE),
                GetTestData("value: -32768", Short.MIN_VALUE),
                GetTestData("value: 32768", Short.MIN_VALUE), // Overflow
                GetTestData("value: -32769", Short.MAX_VALUE), // Underflow
                GetTestData("value: 1", 1.toShort()),
                GetTestData("value: -1", (-1).toShort()),
            ) { (yaml, value) ->
                ktConfigString<Data<Short>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<Short>>(yaml) shouldBe Data(value)
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
                    ktConfigString<Data<Short>>(yaml)
                }
            }
        }

        context("should get null from config") {
            test("non-nullable") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<Short>>("value: null")
                    }
                exception.message shouldBe "Expected kotlin.Short, but null: value"
            }
            test("nullable") {
                ktConfigString<NullableData<Short>>("value: null") shouldBe Data(null)
            }
        }

        context("should save value to config") {
            withData(
                SaveTestData(123.toShort(), "value: 123\n"),
                SaveTestData(0.toShort(), "value: 0\n"),
                SaveTestData((-456).toShort(), "value: -456\n"),
                SaveTestData(789.toShort(), "value: 789\n"),
                SaveTestData(15.toShort(), "value: 15\n"),
                SaveTestData(26.toShort(), "value: 26\n"),
                SaveTestData(Short.MAX_VALUE, "value: 32767\n"),
                SaveTestData(Short.MIN_VALUE, "value: -32768\n"),
                SaveTestData(1.toShort(), "value: 1\n"),
                SaveTestData((-1).toShort(), "value: -1\n"),
            ) { (value, yaml) ->
                saveKtConfigString(Data(value)) shouldBe yaml
                saveKtConfigString(NullableData(value)) shouldBe yaml
            }
        }

        context("should save null to config") {
            test("nullable") {
                saveKtConfigString(NullableData<Short>(null)) shouldBe ""
            }
        }
    })
