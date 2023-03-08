# Consumer Biometrics
The [Biometrics](Biometrics.kt) interface provides methods for detecting biometric availability, registering, authenticating, and removing biometrics identifiers.

Before beginning a biometric registration, there are two important calls to make:
1. `StytchClient.biometrics.areBiometricsAvailable()` will tell you if biometrics are possible on this device, given it's sensors, Android version, existing registrations, etc.
2. `StytchClient.biometrics.isUsingKeystore()` will report whether or not this device has a reliable version of the Android KeyStore. If it does not, there may be issues creating encryption keys, as well as implications on where these keys are stored. The safest approach is to not offer biometrics if this returns `false`, but it is possible to force a registration with an unreliable KeyStore.

If you would like to begin a biometric registration flow, call the `StytchClient.biometrics.register()` method. By default, this method will disallow registrations when the device has an unreliable Android KeyStore. If you are sure you wish to offer biometric authentication on those devices, you must set `allowFallbackToCleartext` to `true` in the `Biometrics.RegisterParameters`.

To determine if a biometric registration exists, call the `StytchClient.biometrics.isRegistrationAvailable()` method.

If a biometric registration exists, you can call the `StytchClient.biometrics.authenticate()` method to begin the authentication flow.

To remove a biometric registration, call `StytchClient.biometrics.removeRegistration()`. This will remove the registration from both the device and the Stytch User object.