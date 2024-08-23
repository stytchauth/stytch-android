# Package com.stytch.sdk.consumer.passkeys
The [Passkeys](Passkeys.kt) interface provides methods for registering and authenticating with Passkeys.

To enable passkey support for your Android app, associate your app with a website that your app owns. You can declare this association by following the instructions [here](https://developer.android.com/training/sign-in/passkeys#add-support-dal)

Once you've associated your app and domain, you can use the below methods to register and authenticate with the Stytch Passkeys product.

Call `StytchClient.passkeys.register()` to create a new Passkey registration.

Call `StytchClient.passkeys.authenticate()` to authenticate a user with an existing Passkey.

Call `StytchClient.passkeys.update()` to update the name of an existing Passkey.
