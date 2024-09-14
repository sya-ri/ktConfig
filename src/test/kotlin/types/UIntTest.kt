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
class UIntTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: 123", 123u),
                GetTestData("value: 0", 0u),
                GetTestData("value: 4294967295", UInt.MAX_VALUE),
                GetTestData("value: 1", 1u),
                GetTestData("value: 2147483648", 2147483648u),
                GetTestData("value: 4294967296", 0u), // Overflow
                GetTestData("value: -1", UInt.MAX_VALUE), // Underflow
            ) { (yaml, value) ->
                ktConfigString<Data<UInt>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<UInt>>(yaml) shouldBe Data(value)
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
                    ktConfigString<Data<UInt>>(yaml)
                }
            }
        }

        context("should get null from config") {
            test("non-nullable") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<UInt>>("value: null")
                    }
                exception.message shouldBe "Expected kotlin.UInt, but null: value"
            }
            test("nullable") {
                ktConfigString<NullableData<UInt>>("value: null") shouldBe Data(null)
            }
        }

        context("should save value to config") {
            withData(
                SaveTestData(123u, "value: 123\n"),
                SaveTestData(0u, "value: 0\n"),
                SaveTestData(UInt.MAX_VALUE, "value: 4294967295\n"),
                SaveTestData(1u, "value: 1\n"),
                SaveTestData(2147483648u, "value: 2147483648\n"),
            ) { (value, yaml) ->
                saveKtConfigString(Data(value)) shouldBe yaml
                saveKtConfigString(NullableData(value)) shouldBe yaml
            }
        }

        context("should save null to config") {
            test("nullable") {
                saveKtConfigString(NullableData<UInt>(null)) shouldBe ""
            }
        }
    })
