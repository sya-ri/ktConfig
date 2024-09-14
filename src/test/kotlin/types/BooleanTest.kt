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
class BooleanTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: true", true),
                GetTestData("value: false", false),
                GetTestData("value: 'true'", true),
                GetTestData("value: 'false'", false),
                GetTestData("value: yes", true),
                GetTestData("value: no", false),
                GetTestData("value: 'YES'", true),
                GetTestData("value: 'NO'", false),
            ) { (yaml, value) ->
                ktConfigString<Data<Boolean>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<Boolean>>(yaml) shouldBe Data(value)
            }
        }

        context("should handle invalid config values") {
            withData(
                "value: 'not a boolean'",
                "value: '123abc'",
                "value: ''",
                "value: '1'",
                "value: '0'",
            ) { yaml ->
                shouldThrow<TypeMismatchException> {
                    ktConfigString<Data<Boolean>>(yaml)
                }
            }
        }

        context("should get null from config") {
            test("non-nullable") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<Boolean>>("value: null")
                    }
                exception.message shouldBe "Expected kotlin.Boolean, but null: value"
            }
            test("nullable") {
                ktConfigString<NullableData<Boolean>>("value: null") shouldBe Data(null)
            }
        }

        context("should save value to config") {
            withData(
                SaveTestData(true, "value: true\n"),
                SaveTestData(false, "value: false\n"),
            ) { (value, yaml) ->
                saveKtConfigString(Data(value)) shouldBe yaml
                saveKtConfigString(NullableData(value)) shouldBe yaml
            }
        }

        context("should save null to config") {
            test("nullable") {
                saveKtConfigString(NullableData<Boolean>(null)) shouldBe ""
            }
        }
    })
