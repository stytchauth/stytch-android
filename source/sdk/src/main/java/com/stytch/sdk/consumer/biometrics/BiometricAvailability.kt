package com.stytch.sdk.consumer.biometrics

import androidx.biometric.BiometricManager
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated

/**
 * Wrapper around BiometricManager results to provide friendly messaging for developers
 */
@JacocoExcludeGenerated
public sealed class BiometricAvailability {
    /**
     * Status indicating that biometrics are not available on this device for some reason
     * @property reason a reason why biometrics are unavailable
     */
    public data class Unavailable(
        public val reason: Reason,
    ) : BiometricAvailability() {
        /**
         * An enum describing why biometrics are unavailable on this device
         * @property message a string representation of why biometrics are unavailable
         */

        public enum class Reason(
            public val message: String,
        ) {
            /**
             * Status indicating that the biometric key failed to generate. Biometrics cannot proceed.
             */
            BIOMETRIC_KEY_GENERATION_FAILED("Biometric key generation failed."),

            /**
             * Status indicating that this device has no biometric hardware.
             */
            BIOMETRIC_ERROR_NO_HARDWARE("No biometric features available on this device."),

            /**
             * Status indicating that biometric hardware features are currently unavailable.
             */
            BIOMETRIC_ERROR_HW_UNAVAILABLE("Biometric features are currently unavailable."),

            /**
             * Status indicating that there are no biometrics currently enrolled on device.
             */
            BIOMETRIC_ERROR_NONE_ENROLLED("No biometrics currently enrolled on device."),

            /**
             * Status indicating that a security vulnerability has been discovered with one or more hardware sensors.
             */
            BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED(
                "A security vulnerability has been discovered with one or more hardware sensors.",
            ),

            /**
             * Status indicating that the requested options are incompatible with the current Android version.
             */
            BIOMETRIC_ERROR_UNSUPPORTED(
                "The requested biometrics options are incompatible with the current Android version.",
            ),

            /**
             * Status indicating that we do not know if biometrics are supported on this device.
             */
            BIOMETRIC_STATUS_UNKNOWN("Unable to determine whether the user can authenticate."),
        }

        internal companion object {
            fun fromReason(reason: Int) =
                Unavailable(
                    when (reason) {
                        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                            Reason.BIOMETRIC_ERROR_NO_HARDWARE
                        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                            Reason.BIOMETRIC_ERROR_HW_UNAVAILABLE
                        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                            Reason.BIOMETRIC_ERROR_NONE_ENROLLED
                        BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED ->
                            Reason.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED
                        BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED ->
                            Reason.BIOMETRIC_ERROR_UNSUPPORTED
                        BiometricManager.BIOMETRIC_STATUS_UNKNOWN ->
                            Reason.BIOMETRIC_STATUS_UNKNOWN
                        else ->
                            Reason.BIOMETRIC_STATUS_UNKNOWN
                    },
                )
        }
    }

    /**
     * Status indicating that the biometric key has been revoked. Usually this means the user has added a new
     * biometric or deleted all existing biometrics.
     */

    @JacocoExcludeGenerated public data object RegistrationRevoked : BiometricAvailability()

    /**
     * Status indicating that biometrics are available, but no registrations have been made yet
     */

    @JacocoExcludeGenerated public data object AvailableNoRegistrations : BiometricAvailability()

    /**
     * Status indicating that biometrics are available and there is already a biometric registration on device
     */

    @JacocoExcludeGenerated public data object AvailableRegistered : BiometricAvailability()
}
