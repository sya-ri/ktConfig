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
class StringListTest :
    FunSpec({
        context("should get value from config") {
            withData(
                GetTestData("value: config string", listOf("config string")),
                GetTestData("value: [config string]", listOf("config string")),
                GetTestData(
                    """
                    value:
                    - config string
                    """.trimIndent(),
                    listOf("config string"),
                ),
                GetTestData("value: [config string, other string]", listOf("config string", "other string")),
                GetTestData(
                    """
                    value:
                    - config string
                    - other string
                    """.trimIndent(),
                    listOf("config string", "other string"),
                ),
            ) { (yaml, value) ->
                ktConfigString<Data<List<String>>>(yaml) shouldBe Data(value)
                ktConfigString<NullableData<List<String>>>(yaml) shouldBe Data(value)
            }
        }

        context("should save value to config") {
            withData(
                SaveTestData(listOf(), "value: []\n"),
                SaveTestData(
                    listOf("config string"),
                    """
                    value:
                    - config string
                    
                    """.trimIndent(),
                ),
                SaveTestData(
                    listOf("config string", "other string"),
                    """
                    value:
                    - config string
                    - other string
                    
                    """.trimIndent(),
                ),
            ) { (value, yaml) ->
                saveKtConfigString(Data(value)) shouldBe yaml
                saveKtConfigString(NullableData(value)) shouldBe yaml
            }
        }
    })
