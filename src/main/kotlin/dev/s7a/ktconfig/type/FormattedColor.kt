package dev.s7a.ktconfig.type

import dev.s7a.ktconfig.UseSerializer
import dev.s7a.ktconfig.exception.InvalidFormatException
import dev.s7a.ktconfig.serializer.StringSerializer
import dev.s7a.ktconfig.serializer.TransformSerializer
import org.bukkit.Bukkit
import org.bukkit.Color

/**
 * Type alias for [Color] that uses [FormattedColorSerializer] for string-based serialization.
 *
 * Represents a color with format:
 * - Prefix: '#' (optional)
 * - AA: Alpha component in hexadecimal (00-FF) (optional)
 * - RR: Red component in hexadecimal (00-FF)
 * - GG: Green component in hexadecimal (00-FF)
 * - BB: Blue component in hexadecimal (00-FF)
 *
 * @since 2.0.0
 */
typealias FormattedColor =
    @UseSerializer(FormattedColorSerializer::class)
    Color

/**
 * Serializer for converting [Color] objects to and from string format.
 *
 * String format:
 * - Prefix: '#' (optional)
 * - AA: Alpha component in hexadecimal (00-FF) (optional)
 * - RR: Red component in hexadecimal (00-FF)
 * - GG: Green component in hexadecimal (00-FF)
 * - BB: Blue component in hexadecimal (00-FF)
 *
 * @throws InvalidFormatException if the string format is invalid
 * @since 2.0.0
 */
object FormattedColorSerializer : TransformSerializer.Keyable<Color, String>(StringSerializer) {
    /**
     * Indicates whether the current Minecraft version supports an alpha channel in colors.
     *
     * This property attempts to check for the existence of the `alpha` field in the [Color] class.
     * If the field is not found, it means the Minecraft version does not support alpha transparency.
     *
     * @since 2.1.0
     */
    val isSupportedAlpha =
        try {
            Color::class.java.getDeclaredField("alpha")
            true
        } catch (_: NoSuchFieldException) {
            Bukkit.getLogger().warning("Color#alpha is not found. This version of Minecraft does not support alpha.")
            false
        }

    override fun decode(value: String): Color {
        val regex = "^#?([0-9A-Fa-f]{6,8})$".toRegex()
        val result = regex.find(value) ?: throw InvalidFormatException(value, "[#]?[AA]?RRGGBB")
        val hex = result.groupValues[1]
        return when {
            hex.length == 6 -> {
                Color.fromRGB(hex.toInt(16))
            }

            isSupportedAlpha -> {
                Color.fromARGB(hex.toInt(16))
            }

            else -> {
                throw InvalidFormatException(value, "[#]?RRGGBB")
            }
        }
    }

    override fun encode(value: Color) =
        buildString {
            append('#')
            if (isSupportedAlpha && value.alpha != 255) {
                append(value.alpha.toString(16).padStart(2, '0'))
            }
            append(value.red.toString(16).padStart(2, '0'))
            append(value.green.toString(16).padStart(2, '0'))
            append(value.blue.toString(16).padStart(2, '0'))
        }
}
