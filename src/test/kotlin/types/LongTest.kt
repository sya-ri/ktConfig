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
class LongTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: 123", 123L),
                GetTestData("value: 0", 0L),
                GetTestData("value: -456", -456L),
                GetTestData("value: '789'", 789L),
                GetTestData("value: 0b1111", 15L),
                GetTestData("value: 0x1A", 26L),
                GetTestData("value: 9223372036854775807", Long.MAX_VALUE),
                GetTestData("value: -9223372036854775808", Long.MIN_VALUE),
                GetTestData("value: 9223372036854775808", Long.MIN_VALUE), // Overflow
                GetTestData("value: -9223372036854775809", Long.MAX_VALUE), // Underflow
                GetTestData("value: 1", 1L),
                GetTestData("value: -1", -1L),
            ) { (yaml, value) ->
                ktConfigString<Data<Long>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<Long>>(yaml) shouldBe Data(value)
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
                    ktConfigString<Data<Long>>(yaml)
                }
            }
        }

        context("should get null from config") {
            test("non-nullable") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<Long>>("value: null")
                    }
                exception.message shouldBe "Expected kotlin.Long, but null: value"
            }
            test("nullable") {
                ktConfigString<NullableData<Long>>("value: null") shouldBe Data(null)
            }
        }

        context("should save value to config") {
            withData(
                SaveTestData(123L, "value: 123\n"),
                SaveTestData(0L, "value: 0\n"),
                SaveTestData(-456L, "value: -456\n"),
                SaveTestData(789L, "value: 789\n"),
                SaveTestData(15L, "value: 15\n"),
                SaveTestData(26L, "value: 26\n"),
                SaveTestData(Long.MAX_VALUE, "value: 9223372036854775807\n"),
                SaveTestData(Long.MIN_VALUE, "value: -9223372036854775808\n"),
                SaveTestData(1L, "value: 1\n"),
                SaveTestData(-1L, "value: -1\n"),
            ) { (value, yaml) ->
                saveKtConfigString(Data(value)) shouldBe yaml
                saveKtConfigString(NullableData(value)) shouldBe yaml
            }
        }

        context("should save null to config") {
            test("nullable") {
                saveKtConfigString(NullableData<Long>(null)) shouldBe ""
            }
        }
    })
