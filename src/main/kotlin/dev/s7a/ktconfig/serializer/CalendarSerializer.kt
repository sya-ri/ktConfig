package dev.s7a.ktconfig.serializer

import java.util.Calendar
import java.util.Date
import java.util.TimeZone

/**
 * Serializer for [Calendar] type.
 * Transforms between [Calendar] and [Date] types using [DateSerializer] as the base serializer.
 *
 * @since 2.0.0
 */
object CalendarSerializer : TransformSerializer.Keyable<Calendar, Date>(DateSerializer) {
    override fun transform(value: Date): Calendar =
        Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            this.time = value
        }

    override fun transformBack(value: Calendar): Date = value.time
}
