package dev.s7a.ktconfig.serializer

import java.time.YearMonth

/**
 * Serializer for [YearMonth] that transforms between [YearMonth] and [String] representations.
 * Uses ISO-8601 year format for string representation.
 *
 * @since 2.0.0
 */
object YearMonthSerializer : TransformSerializer<YearMonth, String>(StringSerializer) {
    override fun decode(value: String): YearMonth = YearMonth.parse(value)

    override fun encode(value: YearMonth) = value.toString()
}
