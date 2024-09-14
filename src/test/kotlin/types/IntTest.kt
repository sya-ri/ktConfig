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
class IntTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: 123", 123),
                GetTestData("value: 0", 0),
                GetTestData("value: -456", -456),
                GetTestData("value: '789'", 789),
                GetTestData("value: 0b1111", 15),
                GetTestData("value: 0x1A", 26),
                GetTestData("value: 2147483647", Int.MAX_VALUE),
                GetTestData("value: -2147483648", Int.MIN_VALUE),
                GetTestData("value: 2147483648", Int.MIN_VALUE), // Overflow
                GetTestData("value: -2147483649", Int.MAX_VALUE), // Underflow
                GetTestData("value: 1", 1),
                GetTestData("value: -1", -1),
            ) { (yaml, value) ->
                ktConfigString<Data<Int>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<Int>>(yaml) shouldBe Data(value)
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
                    ktConfigString<Data<Int>>(yaml)
                }
            }
        }

        context("should get null from config") {
            test("non-nullable") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<Int>>("value: null")
                    }
                exception.message shouldBe "Expected kotlin.Int, but null: value"
            }
            test("nullable") {
                ktConfigString<NullableData<Int>>("value: null") shouldBe Data(null)
            }
        }

        context("should save value to config") {
            withData(
                SaveTestData(123, "value: 123\n"),
                SaveTestData(0, "value: 0\n"),
                SaveTestData(-456, "value: -456\n"),
                SaveTestData(789, "value: 789\n"),
                SaveTestData(15, "value: 15\n"),
                SaveTestData(26, "value: 26\n"),
                SaveTestData(Int.MAX_VALUE, "value: 2147483647\n"),
                SaveTestData(Int.MIN_VALUE, "value: -2147483648\n"),
                SaveTestData(1, "value: 1\n"),
                SaveTestData(-1, "value: -1\n"),
            ) { (value, yaml) ->
                saveKtConfigString(Data(value)) shouldBe yaml
                saveKtConfigString(NullableData(value)) shouldBe yaml
            }
        }

        context("should save null to config") {
            test("nullable") {
                saveKtConfigString(NullableData<Int>(null)) shouldBe ""
            }
        }
    })
