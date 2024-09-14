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
class UByteTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: 123", 123.toUByte()),
                GetTestData("value: 0", 0.toUByte()),
                GetTestData("value: 255", UByte.MAX_VALUE),
                GetTestData("value: 1", 1.toUByte()),
                GetTestData("value: 128", 128.toUByte()),
                GetTestData("value: 256", 0.toUByte()), // Overflow
                GetTestData("value: -1", UByte.MAX_VALUE), // Underflow
            ) { (yaml, value) ->
                ktConfigString<Data<UByte>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<UByte>>(yaml) shouldBe Data(value)
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
                    ktConfigString<Data<UByte>>(yaml)
                }
            }
        }

        context("should get null from config") {
            test("non-nullable") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<UByte>>("value: null")
                    }
                exception.message shouldBe "Expected kotlin.UByte, but null: value"
            }
            test("nullable") {
                ktConfigString<NullableData<UByte>>("value: null") shouldBe Data(null)
            }
        }

        context("should save value to config") {
            withData(
                SaveTestData(123.toUByte(), "value: 123\n"),
                SaveTestData(0.toUByte(), "value: 0\n"),
                SaveTestData(UByte.MAX_VALUE, "value: 255\n"),
                SaveTestData(1.toUByte(), "value: 1\n"),
                SaveTestData(128.toUByte(), "value: 128\n"),
            ) { (value, yaml) ->
                saveKtConfigString(Data(value)) shouldBe yaml
                saveKtConfigString(NullableData(value)) shouldBe yaml
            }
        }

        context("should save null to config") {
            test("nullable") {
                saveKtConfigString(NullableData<UByte>(null)) shouldBe ""
            }
        }
    })
