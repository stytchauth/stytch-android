package com.stytch.sdk.consumer.biometrics

import androidx.fragment.app.FragmentActivity
import com.stytch.sdk.common.Constants
import com.stytch.sdk.consumer.BiometricsAuthResponse

/**
 * The Biometrics interface provides methods for detecting biometric availability, registering, authenticating, and
 * removing biometrics identifiers.
 */
public interface Biometrics {
    /**
     * Data class used for wrapping parameters used with Biometrics registration flow
     * @property context is the calling FragmentActivity
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     * @property allowFallbackToCleartext opts-in to allowing biometric registrations when the KeyStore is unreliable.
     * If this is set to `false` (default behavior), biometric registrations will fail when the KeyStore is not used.
     * @property promptData is an optional biometric prompt configuration. If one is not provided a default will be
     * created
     * @property allowDeviceCredentials opts-in to allowing the use of non-biometric device credentials (PIN, Pattern)
     * as a fallback (on Android versions greater than Q)
     */
    public data class RegisterParameters(
        val context: FragmentActivity,
        val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
        val allowFallbackToCleartext: Boolean = false,
        val promptData: PromptData? = null,
        val allowDeviceCredentials: Boolean = false,
    )

    /**
     * Data class used for wrapping parameters used with Biometrics authentication flow
     * @property context is the calling FragmentActivity
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     * @property promptData is an optional biometric prompt configuration. If one is not provided a default will be
     * created
     */
    public data class AuthenticateParameters(
        val context: FragmentActivity,
        val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
        val promptData: PromptData? = null,
    )

    /**
     * Data class used for wrapping parameters used to create a biometric prompt
     * @property title The title to be displayed in the Biometric Prompt
     * @property subTitle The subtitle to be displayed in the Biometric Prompt
     * @property negativeButtonText "Cancel" button text to display, if device credentials are disallowed
     */
    public data class PromptData(
        val title: String,
        val subTitle: String,
        val negativeButtonText: String,
    )

    /**
     * Indicates if there is an existing biometric registration on device.
     * @param context is the calling FragmentActivity
     * @return Boolean
     */
    public fun isRegistrationAvailable(context: FragmentActivity): Boolean

    /**
     * Indicates if the biometric sensor is available, and provides a reasoning if not
     * @param context is the calling FragmentActivity
     * @param allowDeviceCredentials whether or not you choose to allow the use of non-biometric device credentials
     * (PIN, Pattern) as a fallback (on Android versions greater than Q)
     * @return [BiometricAvailability]
     */
    public fun areBiometricsAvailable(
        context: FragmentActivity,
        allowDeviceCredentials: Boolean = false,
    ): BiometricAvailability

    /**
     * Clears existing biometric registrations stored on device. Useful when removing a user from a given device.
     * Returns true if the registration was successfully removed from device.
     * @return Boolean
     */
    public suspend fun removeRegistration(): Boolean

    /**
     * Clears existing biometric registrations stored on device. Useful when removing a user from a given device.
     * Returns true if the registration was successfully removed from device.
     * @param callback A callback that receives a Boolean
     */
    public fun removeRegistration(callback: (Boolean) -> Unit)

    /**
     * Indicates if the device is device has a reliable version of the Android KeyStore. If it does not, there may be
     * issues creating encryption keys, as well as implications on where these keys are stored. The safest approach is
     * to not offer biometrics if this returns `false`, but it is possible to force a registration with an unreliable
     * KeyStore.
     * @return Boolean
     */
    public fun isUsingKeystore(): Boolean

    /**
     * When a valid/active session exists, this method will add a biometric registration for the current user.
     * The user will later be able to start a new session with biometrics or use biometrics as an additional
     * authentication factor.
     * @param parameters required to register a biometrics key
     * @return [BiometricsAuthResponse]
     */
    public suspend fun register(parameters: RegisterParameters): BiometricsAuthResponse

    /**
     * When a valid/active session exists, this method will add a biometric registration for the current user.
     * The user will later be able to start a new session with biometrics or use biometrics as an additional
     * authentication factor.
     * @param parameters required to register a biometrics key
     * @param callback a callback that receives a [BiometricsAuthResponse]
     */
    public fun register(
        parameters: RegisterParameters,
        callback: (response: BiometricsAuthResponse) -> Unit,
    )

    /**
     * If a valid biometric registration exists, this method confirms the current device owner via the device's built-in
     * biometric reader and returns an updated session object by either starting a new session or adding the biometric
     * factor to an existing session.
     * @param parameters required to authenticate a biometrics key
     * @return [BiometricsAuthResponse]
     */
    public suspend fun authenticate(parameters: AuthenticateParameters): BiometricsAuthResponse

    /**
     * If a valid biometric registration exists, this method confirms the current device owner via the device's built-in
     * biometric reader and returns an updated session object by either starting a new session or adding the biometric
     * factor to an existing session.
     * @param parameters required to authenticate a biometrics key
     * @param callback a callback that receives a [BiometricsAuthResponse]
     */
    public fun authenticate(
        parameters: AuthenticateParameters,
        callback: (response: BiometricsAuthResponse) -> Unit,
    )
}
