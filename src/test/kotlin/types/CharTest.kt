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
class CharTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: a", 'a'),
                GetTestData("value: 2", 0x2.toChar()),
                GetTestData("value: '2'", '2'),
                GetTestData(
                    """
                    value: !!binary |-
                      Ag==
                    """.trimIndent(),
                    2.toChar(),
                ),
            ) { (yaml, value) ->
                ktConfigString<Data<Char>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<Char>>(yaml) shouldBe Data(value)
            }
        }

        context("should get null from config") {
            test("not-null") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<Char>>("value: null")
                    }
                exception.message shouldBe "Expected kotlin.Char, but null: value"
            }
            test("nullable") {
                ktConfigString<NullableData<Char>>("value: null") shouldBe Data(null)
            }
        }

        context("should save value to config") {
            withData(
                SaveTestData('a', "value: a\n"),
                SaveTestData('2', "value: '2'\n"),
                SaveTestData(
                    2.toChar(),
                    """
                    value: !!binary |-
                      Ag==
                    
                    """.trimIndent(),
                ),
            ) { (value, yaml) ->
                saveKtConfigString(Data(value)) shouldBe yaml
                saveKtConfigString(NullableData(value)) shouldBe yaml
            }
        }

        context("should save null to config") {
            test("nullable") {
                saveKtConfigString(NullableData<Char>(null)) shouldBe ""
            }
        }
    })
