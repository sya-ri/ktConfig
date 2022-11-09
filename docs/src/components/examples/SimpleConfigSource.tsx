import CodeBlock from "@theme/CodeBlock";
import React from "react";

const code =
`class Main : JavaPlugin() { 
    @Comment("This is header comments")
    data class SimpleConfig(
        val message: String = "You can use default values"
    )

    override fun onEnable() {
        val config = this.ktConfigFile("config.yml", SimpleConfig())
        logger.info(config.message)
    }
}`;

export default function SimpleConfigSource(): JSX.Element {
    return <CodeBlock language="kotlin" title="Main.kt">{code}</CodeBlock>;
}
