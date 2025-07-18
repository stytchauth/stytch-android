package com.stytch.sdk.ui.shared.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.stytch.sdk.R
import java.util.regex.Pattern

private val EMAIL_ADDRESS_PATTERN =
    Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+",
    )

internal fun String.isValidEmailAddress(): Boolean = EMAIL_ADDRESS_PATTERN.matcher(this).matches()

@Composable
internal fun String.mapZxcvbnToStringResource(): String =
    when (this) {
        "Use a few words, avoid common phrases." -> stringResource(R.string.stytch_zxcvbn_suggestion_1)
        "No need for symbols, digits, or uppercase letters." ->
            stringResource(
                R.string.stytch_zxcvbn_suggestion_2,
            )
        "Add another word or two. Uncommon words are better." ->
            stringResource(
                R.string.stytch_zxcvbn_suggestion_3,
            )
        "Use a longer keyboard pattern with more turns." -> stringResource(R.string.stytch_zxcvbn_suggestion_4)
        "Avoid repeated words and characters." -> stringResource(R.string.stytch_zxcvbn_suggestion_5)
        "Avoid sequences." -> stringResource(R.string.stytch_zxcvbn_suggestion_6)
        "Avoid recent years." -> stringResource(R.string.stytch_zxcvbn_suggestion_7)
        "Avoid years that are associated with you." -> stringResource(R.string.stytch_zxcvbn_suggestion_8)
        "Avoid dates and years that are associated with you." ->
            stringResource(
                R.string.stytch_zxcvbn_suggestion_9,
            )
        "Capitalization doesn't help very much." -> stringResource(R.string.stytch_zxcvbn_suggestion_10)
        "All-uppercase is almost as easy to guess as all-lowercase." ->
            stringResource(
                R.string.stytch_zxcvbn_suggestion_11,
            )
        "Reversed words aren't much harder to guess." -> stringResource(R.string.stytch_zxcvbn_suggestion_12)
        "Predictable substitutions like '@' instead of 'a' don't help very much." ->
            stringResource(
                R.string.stytch_zxcvbn_suggestion_13,
            )
        "Short keyboard patterns are easy to guess." -> stringResource(R.string.stytch_zxcvbn_suggestion_14)
        "Straight rows of keys are easy to guess." -> stringResource(R.string.stytch_zxcvbn_suggestion_15)
        "Repeats like \"abcabcabc\" are only slightly harder to guess than \"abc\"." ->
            stringResource(
                R.string.stytch_zxcvbn_suggestion_16,
            )
        "Repeats like \"aaa\" are easy to guess." -> stringResource(R.string.stytch_zxcvbn_suggestion_17)
        "Sequences like \"abc\" or \"6543\" are easy to guess." ->
            stringResource(
                R.string.stytch_zxcvbn_suggestion_18,
            )
        "Recent years are easy to guess." -> stringResource(R.string.stytch_zxcvbn_suggestion_19)
        "Dates are often easy to guess." -> stringResource(R.string.stytch_zxcvbn_suggestion_20)
        "This is a top-10 common password." -> stringResource(R.string.stytch_zxcvbn_suggestion_21)
        "This is a top-100 common password." -> stringResource(R.string.stytch_zxcvbn_suggestion_22)
        "This is a very common password." -> stringResource(R.string.stytch_zxcvbn_suggestion_23)
        "This is similar to a commonly used password." -> stringResource(R.string.stytch_zxcvbn_suggestion_24)
        "A word by itself is easy to guess." -> stringResource(R.string.stytch_zxcvbn_suggestion_25)
        "Names and surnames by themselves are easy to guess." ->
            stringResource(
                R.string.stytch_zxcvbn_suggestion_26,
            )
        "Common names and surnames are easy to guess." -> stringResource(R.string.stytch_zxcvbn_suggestion_27)
        else -> this
    }
