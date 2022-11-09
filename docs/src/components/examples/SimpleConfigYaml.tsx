import CodeBlock from "@theme/CodeBlock";
import React from "react";

const code =
`# This is header comments

message: You can use default values`;

export default function SimpleConfigYaml(): JSX.Element {
    return <CodeBlock language="yaml" title="config.yml">{code}</CodeBlock>;
}
