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
class StringTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: config string", "config string"),
                GetTestData("value: a", "a"),
                GetTestData("value: 'null'", "null"),
                GetTestData("value: 12", "12"),
                GetTestData(
                    """
                    value: !!binary |-
                      Ag==
                    """.trimIndent(),
                    2.toChar().toString(),
                ),
                GetTestData("value: ''", ""),
                GetTestData("value: '特殊文字!@#\$%^&*()'", "特殊文字!@#\$%^&*()"),
                GetTestData("value: '123abc'", "123abc"),
                GetTestData(
                    """
                    value: |
                      multiple
                      lines
                    """.trimIndent(),
                    "multiple\nlines",
                ),
            ) { (yaml, value) ->
                ktConfigString<Data<String>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<String>>(yaml) shouldBe Data(value)
            }
        }

        context("should get null from config") {
            test("not-null") {
                val exception =
                    shouldThrow<TypeMismatchException> {
                        ktConfigString<Data<String>>("value: null")
                    }
                exception.message shouldBe "Expected kotlin.String, but null: value"
            }
            test("nullable") {
                ktConfigString<NullableData<String>>("value: null") shouldBe Data(null)
            }
        }

        context("should save value to config") {
            withData(
                SaveTestData("config string", "value: config string\n"),
                SaveTestData("null", "value: 'null'\n"),
                SaveTestData("12", "value: '12'\n"),
                SaveTestData(
                    2.toChar().toString(),
                    """
                    value: !!binary |-
                      Ag==
                    
                    """.trimIndent(),
                ),
                SaveTestData("", "value: ''\n"),
                SaveTestData(
                    "特殊文字!@#\$%^&*()",
                    """
                    value: 特殊文字!@#${'$'}%^&*()
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    "123abc",
                    """
                    value: 123abc
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    "multiple\nlines",
                    """
                    value: |-
                      multiple
                      lines
                    
                    """.trimIndent(),
                ),
            ) { (value, yaml) ->
                saveKtConfigString(Data(value)) shouldBe yaml
                saveKtConfigString(NullableData(value)) shouldBe yaml
            }
        }

        context("should save null to config") {
            test("nullable") {
                saveKtConfigString(NullableData<String>(null)) shouldBe ""
            }
        }
    })
