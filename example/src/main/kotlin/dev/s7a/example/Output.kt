package dev.s7a.example

class Output(
    private val plugin: ExamplePlugin,
) {
    private val outputFile = plugin.dataFolder.resolve("output.txt")
    private val errorFile = plugin.dataFolder.resolve("error.txt")

    init {
        plugin.dataFolder.mkdirs()

        // Recreate output.txt and error.txt
        outputFile.delete()
        errorFile.delete()
        outputFile.createNewFile()
        errorFile.createNewFile()
    }

    fun info(message: String = "") {
        outputFile.appendText("$message\n")
        message.lines().forEach(plugin.logger::info)
    }

    fun error(message: String) {
        errorFile.appendText("$message\n")
        message.lines().forEach(plugin.logger::severe)
    }
}
