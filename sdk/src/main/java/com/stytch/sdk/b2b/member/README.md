# Package com.stytch.sdk.b2b.member
The [Member](Member.kt) interface provides methods for retrieving the current authenticated user.

You can choose to get the local representation of the user, without making a network request, with the `StytchClient.member.getSync()` method.

If you want to get the freshest representation of the user from the Stytch servers, use the `StytchClient.member.get()` method.