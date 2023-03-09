# Package com.stytch.sdk.consumer.userManagement
The [UserManagement](UserManagement.kt) interface provides methods for retrieving an authenticated user and deleting authentication factors from an authenticated user.

You can choose to get the local representation of the user, without making a network request, with the `StytchClient.user.getSyncUser()` method.

If you want to get the freshest representation of the user from the Stytch servers, use the `StytchClient.user.getUser()` method.

To remove an authentication factor from a user, use the `StytchClient.user.deleteFactor()` method.