package dev.s7a.ktconfig.ksp

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.buildCodeBlock

/**
 * ["element1", "element2"] -> 'listOf("element1", "element2")'
 */
fun List<String>.asLiteralList() = "listOf(${joinToString(", ") { "\"$it\"" } })"

inline fun FunSpec.Builder.addControlFlowCode(
    controlFlow: String,
    vararg args: Any?,
    block: CodeBlock.Builder.() -> Unit,
) {
    addCode(controlFlowCode(controlFlow, *args, block = block))
}

inline fun CodeBlock.Builder.addControlFlowCode(
    controlFlow: String,
    vararg args: Any?,
    block: CodeBlock.Builder.() -> Unit,
) {
    add(controlFlowCode(controlFlow, *args, block = block))
}

inline fun controlFlowCode(
    controlFlow: String,
    vararg args: Any?,
    block: CodeBlock.Builder.() -> Unit,
) = buildCodeBlock {
    addControlFlow(controlFlow, *args, block = block)
}

inline fun CodeBlock.Builder.addControlFlow(
    controlFlow: String,
    vararg args: Any?,
    block: CodeBlock.Builder.() -> Unit,
) = beginControlFlow(controlFlow, *args).apply(block).endControlFlow()
