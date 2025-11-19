package dev.s7a.ktconfig.serializer

import dev.s7a.ktconfig.exception.InvalidFormatException
import dev.s7a.ktconfig.exception.UnsupportedConvertException
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

/**
 * Serializer for [Date] type.
 * Handles conversion of [Date] values to and from YAML configuration.
 * Supports both timestamp formats like "2023-12-31T23:59:59" and date formats like "2023-12-31".
 *
 * @since 2.0.0
 */
object DateSerializer : Serializer.Keyable<Date> {
    override fun serialize(value: Date): Date = value

    override fun deserialize(value: Any): Date =
        when (value) {
            is Calendar -> value.time
            is Date -> value
            is String -> parse(value)
            else -> throw UnsupportedConvertException(value::class, Date::class)
        }

    private val timestampRegex =
        Regex(
            """^([0-9]{4})-([0-9]{1,2})-([0-9]{1,2})(?:(?:[Tt]|\s+)([0-9]{1,2}):([0-9]{2}):([0-9]{2})(?:\.([0-9]*))?(?:\s*(?:Z|([-+][0-9]{1,2})(?::([0-9]{2})?)?))?)?$""",
        )
    private val ymdRegex = Regex("""^([0-9]{4})-([0-9]{1,2})-([0-9]{1,2})$""")

    private fun parse(text: String): Date {
        timestampRegex.matchEntire(text)?.let { match ->
            val (year, month, day, hour, minute, second, ms, tz, tzm) = match.destructured
            val timezone =
                when {
                    tz.isEmpty() -> "UTC"
                    tzm.isEmpty() -> "GMT$tz"
                    else -> "GMT$tz:$tzm"
                }
            val calendar = Calendar.getInstance(TimeZone.getTimeZone(timezone))
            calendar.set(
                year.toInt(),
                month.toInt() - 1,
                day.toInt(),
                hour.toIntOrNull() ?: 0,
                minute.toIntOrNull() ?: 0,
                second.toIntOrNull() ?: 0,
            )
            if (ms.isNotEmpty()) {
                calendar.set(Calendar.MILLISECOND, ms.padEnd(3, '0').take(3).toInt())
            } else {
                calendar.set(Calendar.MILLISECOND, 0)
            }
            return calendar.time
        }

        ymdRegex.matchEntire(text)?.let { match ->
            val (year, month, day) = match.destructured
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.set(year.toInt(), month.toInt() - 1, day.toInt(), 0, 0, 0)
            return calendar.time
        }

        throw InvalidFormatException(text)
    }
}
