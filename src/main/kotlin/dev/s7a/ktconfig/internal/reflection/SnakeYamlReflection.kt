package dev.s7a.ktconfig.internal.reflection

import dev.s7a.ktconfig.internal.reflection.SnakeYamlReflection.SafeConstructorMethod
import dev.s7a.ktconfig.internal.reflection.SnakeYamlReflection.ScalarNodeConstructorMethod
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.constructor.SafeConstructor
import org.yaml.snakeyaml.error.Mark
import org.yaml.snakeyaml.nodes.ScalarNode
import org.yaml.snakeyaml.nodes.Tag

internal object SnakeYamlReflection {
    private fun interface SafeConstructorMethod {
        fun invoke(): SafeConstructor
    }

    private fun interface ScalarNodeConstructorMethod {
        fun invoke(value: String): ScalarNode
    }

    private val safeConstructorMethod: SafeConstructorMethod
    private val scalarNodeConstructorMethod: ScalarNodeConstructorMethod

    init {
        val safeConstructorClass = SafeConstructor::class.java
        safeConstructorMethod =
            try {
                val loaderOptionsClass = Class.forName("org.yaml.snakeyaml.LoaderOptions")
                val constructor = safeConstructorClass.getConstructor(loaderOptionsClass)
                val loaderOptionsConstructor = loaderOptionsClass.getConstructor()
                SafeConstructorMethod {
                    constructor.newInstance(loaderOptionsConstructor.newInstance())
                }
            } catch (_: ClassNotFoundException) {
                val constructor = safeConstructorClass.getConstructor()
                SafeConstructorMethod {
                    constructor.newInstance()
                }
            }
        val scalarNodeClass = ScalarNode::class.java
        scalarNodeConstructorMethod =
            try {
                val constructor =
                    scalarNodeClass.getConstructor(
                        Tag::class.java,
                        String::class.java,
                        Mark::class.java,
                        Mark::class.java,
                        DumperOptions.ScalarStyle::class.java,
                    )
                ScalarNodeConstructorMethod {
                    constructor.newInstance(Tag.STR, it, null, null, DumperOptions.ScalarStyle.PLAIN)
                }
            } catch (_: NoSuchMethodException) {
                val constructor =
                    scalarNodeClass.getConstructor(
                        Tag::class.java,
                        String::class.java,
                        Mark::class.java,
                        Mark::class.java,
                        Char::class.java,
                    )
                ScalarNodeConstructorMethod {
                    constructor.newInstance(Tag.STR, it, null, null, null)
                }
            }
    }

    fun getSafeConstructor(): SafeConstructor {
        return safeConstructorMethod.invoke()
    }

    fun getScalarNode(value: String): ScalarNode {
        return scalarNodeConstructorMethod.invoke(value)
    }
}
