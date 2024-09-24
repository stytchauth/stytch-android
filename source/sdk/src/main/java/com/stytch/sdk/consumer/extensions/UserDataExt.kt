package com.stytch.sdk.consumer.extensions

import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.consumer.biometrics.BIOMETRIC_KEY_NAME
import com.stytch.sdk.consumer.biometrics.KEYS_REQUIRED_FOR_REGISTRATION
import com.stytch.sdk.consumer.biometrics.LAST_USED_BIOMETRIC_REGISTRATION_ID
import com.stytch.sdk.consumer.network.models.UserData

internal fun UserData.keepLocalBiometricRegistrationsInSync(storageHelper: StorageHelper) {
    // If a local biometric registration exists, but it's not present on the UserData object
    // delete the local registration, as it is no longer valid
    val localRegistrationId = storageHelper.loadValue(LAST_USED_BIOMETRIC_REGISTRATION_ID)
    if (localRegistrationId != null && !biometricRegistrations.map { it.id }.contains(localRegistrationId)) {
        KEYS_REQUIRED_FOR_REGISTRATION.forEach { storageHelper.deletePreference(it) }
        storageHelper.keyStore.deleteEntry(BIOMETRIC_KEY_NAME)
    }
}
