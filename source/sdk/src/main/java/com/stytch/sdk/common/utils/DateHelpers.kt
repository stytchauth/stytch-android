package com.stytch.sdk.common.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Format a date as ISO-8601, using the appropriate date and time patterns. See: https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html
internal val ISO_DATE_FORMATTER = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US)

internal val SHORT_FORM_DATE_FORMATTER = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)

internal fun String?.getDateOrMin(minimum: Date = Date(0L)): Date =
    this?.let { date ->
        try {
            ISO_DATE_FORMATTER.parse(date)
        } catch (e: ParseException) {
            SHORT_FORM_DATE_FORMATTER.parse(date)
        }
    } ?: minimum
