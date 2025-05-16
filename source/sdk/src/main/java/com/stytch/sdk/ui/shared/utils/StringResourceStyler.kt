package com.stytch.sdk.ui.shared.utils

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.SpannedString
import android.text.style.CharacterStyle
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.annotation.StringRes
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

private object StringResourceStyler {
    fun getCharacterStyle(
        key: String,
        value: String,
    ): CharacterStyle? =
        when (key) {
            KEY_STYLE -> {
                when (value) {
                    VALUE_NORMAL -> StyleSpan(Typeface.NORMAL)
                    VALUE_BOLD -> StyleSpan(Typeface.BOLD)
                    VALUE_ITALIC -> StyleSpan(Typeface.ITALIC)
                    VALUE_BOLD_ITALIC -> StyleSpan(Typeface.BOLD_ITALIC)
                    VALUE_STRIKE -> StrikethroughSpan()
                    VALUE_UNDERLINE -> UnderlineSpan()
                    else -> null
                }
            }
            else -> null
        }

    const val KEY_ARGUMENT = "arg"
    const val KEY_STYLE = "style"

    const val VALUE_NORMAL = "normal"
    const val VALUE_BOLD = "bold"
    const val VALUE_ITALIC = "italic"
    const val VALUE_BOLD_ITALIC = "bold_italic"
    const val VALUE_STRIKE = "strike"
    const val VALUE_UNDERLINE = "underline"
}

internal fun Context.getStyledText(
    @StringRes resId: Int,
    vararg args: Any,
): AnnotatedString {
    val text = getText(resId) as SpannedString
    return SpannableStringBuilder(text)
        .apply {
            getSpans(0, length, android.text.Annotation::class.java).forEach<android.text.Annotation> { annotation ->
                val start = getSpanStart(annotation)
                val end = getSpanEnd(annotation)

                when (annotation.key) {
                    StringResourceStyler.KEY_ARGUMENT ->
                        replace(
                            start,
                            end,
                            String.format(annotation.value, *args),
                        )
                    else -> {
                        annotation.value
                            .split("|")
                            .mapNotNull { StringResourceStyler.getCharacterStyle(annotation.key, it) }
                            .forEach { applyStyle(it, start, end) }
                    }
                }
            }
        }.toAnnotatedString()
}

private fun Spannable.applyStyle(
    characterStyle: CharacterStyle,
    start: Int = 0,
    end: Int = this.length,
) {
    this.setSpan(
        characterStyle,
        start,
        end,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
    )
}

private fun Spannable.toAnnotatedString(): AnnotatedString {
    val builder = AnnotatedString.Builder(this.toString())
    SpanCopier.entries.forEach { copier ->
        getSpans(0, length, copier.spanClass).forEach { span ->
            copier.copySpan(span, getSpanStart(span), getSpanEnd(span), builder)
        }
    }
    return builder.toAnnotatedString()
}

private enum class SpanCopier {
    UNDERLINE {
        override val spanClass = UnderlineSpan::class.java

        override fun copySpan(
            span: Any,
            start: Int,
            end: Int,
            destination: AnnotatedString.Builder,
        ) {
            destination.addStyle(
                style = SpanStyle(textDecoration = TextDecoration.Underline),
                start = start,
                end = end,
            )
        }
    },
    STYLE {
        override val spanClass = StyleSpan::class.java

        override fun copySpan(
            span: Any,
            start: Int,
            end: Int,
            destination: AnnotatedString.Builder,
        ) {
            val styleSpan = span as StyleSpan

            destination.addStyle(
                style =
                    when (styleSpan.style) {
                        Typeface.ITALIC -> SpanStyle(fontStyle = FontStyle.Italic)
                        Typeface.BOLD -> SpanStyle(fontWeight = FontWeight.Bold)
                        Typeface.BOLD_ITALIC -> SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
                        else -> SpanStyle()
                    },
                start = start,
                end = end,
            )
        }
    }, ;

    abstract val spanClass: Class<out CharacterStyle>

    abstract fun copySpan(
        span: Any,
        start: Int,
        end: Int,
        destination: AnnotatedString.Builder,
    )
}
