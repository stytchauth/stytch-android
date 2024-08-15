package com.stytch.sdk.ui.data

import android.os.Parcelable
import com.stytch.sdk.consumer.magicLinks.MagicLinks
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class EMLDetails(
    val parameters: MagicLinks.EmailMagicLinks.Parameters,
) : Parcelable
