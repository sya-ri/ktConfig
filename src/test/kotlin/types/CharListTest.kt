package types

import dev.s7a.ktconfig.ktConfigString
import dev.s7a.ktconfig.saveKtConfigString
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import utils.Data
import utils.GetTestData
import utils.NullableData
import utils.SaveTestData

@Suppress("unused")
class CharListTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: a", listOf('a')),
                GetTestData("value: [a]", listOf('a')),
                GetTestData(
                    """
                    value:
                    - a
                    """.trimIndent(),
                    listOf('a'),
                ),
                GetTestData("value: [a, '6', 9]", listOf('a', '6', 9.toChar())),
                GetTestData(
                    """
                    value:
                    - a
                    - '6'
                    - 9
                    - !!binary |-
                      Ag==
                    """.trimIndent(),
                    listOf('a', '6', 9.toChar(), 2.toChar()),
                ),
            ) { (yaml, value) ->
                ktConfigString<Data<List<Char>>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<List<Char>>>(yaml) shouldBe Data(value)
            }
        }

        context("should save value to config") {
            withData(
                SaveTestData(listOf(), "value: []\n"),
                SaveTestData(
                    listOf('a'),
                    """
                    value:
                    - a
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf('a', '6', 9.toChar(), 2.toChar()),
                    """
                    value:
                    - a
                    - '6'
                    - "\t"
                    - !!binary |-
                      Ag==
                    
                    """.trimIndent(),
                ),
            ) { (value, yaml) ->
                saveKtConfigString(Data(value)) shouldBe yaml
                saveKtConfigString(NullableData(value)) shouldBe yaml
            }
        }
    })
