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
class ULongTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: 123", 123UL),
                GetTestData("value: 0", 0UL),
                GetTestData("value: 18446744073709551615", ULong.MAX_VALUE),
                GetTestData("value: 1", 1UL),
                GetTestData("value: 9223372036854775808", 9223372036854775808UL),
                GetTestData("value: 18446744073709551616", 0UL), // Overflow
                GetTestData("value: -1", ULong.MAX_VALUE), // Underflow
            ) { (yaml, value) ->
                ktConfigString<Data<ULong>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<ULong>>(yaml) shouldBe Data(value)
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
                    ktConfigString<Data<ULong>>(yaml)
                }
            }
        }

        context("should get null from config") {
            test("non-nullable") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<ULong>>("value: null")
                    }
                exception.message shouldBe "Expected kotlin.ULong, but null: value"
            }
            test("nullable") {
                ktConfigString<NullableData<ULong>>("value: null") shouldBe Data(null)
            }
        }

        context("should save value to config") {
            withData(
                SaveTestData(123UL, "value: 123\n"),
                SaveTestData(0UL, "value: 0\n"),
                SaveTestData(ULong.MAX_VALUE, "value: 18446744073709551615\n"),
                SaveTestData(1UL, "value: 1\n"),
                SaveTestData(9223372036854775808UL, "value: 9223372036854775808\n"),
            ) { (value, yaml) ->
                saveKtConfigString(Data(value)) shouldBe yaml
                saveKtConfigString(NullableData(value)) shouldBe yaml
            }
        }

        context("should save null to config") {
            test("nullable") {
                saveKtConfigString(NullableData<ULong>(null)) shouldBe ""
            }
        }
    })
