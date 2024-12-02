package com.stytch.sdk.ui.b2b.data

internal enum class B2BErrorType(
    val description: String,
) {
    Default("Something went wrong. Try again later or contact your admin for help."),
    EmailMagicLink(
        "Something went wrong. Your login link may have expired, been revoked, or been used more than once." +
            "Request a new login link to try again, or contact your admin for help.",
    ),
    Organization(
        "The organization you are looking for could not be found. If you think this is a mistake, contact your admin.",
    ),
    CannotJoinOrgDueToAuthPolicy(
        "Unable to join due to %s's authentication policy. Please contact your admin for more information.",
    ),
}
