package com.stytch.sdk.common.utils

import java.lang.ThreadLocal
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.ConcurrentHashMap

private object SdfPool {
    private data class Key(
        val pattern: String,
        val tzId: String,
        val locale: Locale,
    )

    private val cache = ConcurrentHashMap<Key, ThreadLocal<SimpleDateFormat>>()

    private fun newThreadLocal(
        pattern: String,
        tz: TimeZone,
        locale: Locale,
    ): ThreadLocal<SimpleDateFormat> =
        object : ThreadLocal<SimpleDateFormat>() {
            override fun initialValue(): SimpleDateFormat =
                SimpleDateFormat(pattern, locale).apply {
                    isLenient = false
                    timeZone = tz
                }
        }

    fun get(
        pattern: String,
        tz: TimeZone = TimeZone.getTimeZone("UTC"),
        locale: Locale = Locale.US,
    ): SimpleDateFormat {
        val key = Key(pattern, tz.id, locale)
        var tl = cache[key]
        if (tl == null) {
            val created = newThreadLocal(pattern, tz, locale)
            val prev = cache.putIfAbsent(key, created)
            tl = prev ?: created
        }
        return tl.get()!!
    }
}

private const val ISO_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
private const val SHORT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"

internal val ISO_DATE_FORMATTER get() = SdfPool.get(ISO_PATTERN)
internal val SHORT_FORM_DATE_FORMATTER get() = SdfPool.get(SHORT_PATTERN)

internal fun String?.getDateOrMin(minimum: Date = Date(0L)): Date =
    this?.let { date ->
        try {
            ISO_DATE_FORMATTER.parse(date)
        } catch (_: ParseException) {
            try {
                SHORT_FORM_DATE_FORMATTER.parse(date)
            } catch (_: ParseException) {
                minimum
            }
        }
    } ?: minimum
