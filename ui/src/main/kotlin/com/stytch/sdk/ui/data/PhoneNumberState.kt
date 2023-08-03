package com.stytch.sdk.ui.data

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber

internal data class PhoneNumberState(
    val countryCode: String = "1",
    val phoneNumber: String = "",
    val error: String? = null,
) {
    fun toE164(): String {
        val phone = Phonenumber.PhoneNumber().apply {
            countryCode = this@PhoneNumberState.countryCode.toInt()
            nationalNumber = (this@PhoneNumberState.phoneNumber).toLong()
        }
        return PhoneNumberUtil.getInstance().format(phone, PhoneNumberUtil.PhoneNumberFormat.E164)
    }
}
