package com.stytch.sdk.common.utils

import java.text.SimpleDateFormat

// Format a date as ISO-8601, using the appropriate date and time patterns. See: https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html
internal val ISO_DATE_FORMATTER = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
