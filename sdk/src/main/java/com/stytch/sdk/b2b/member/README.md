# Package com.stytch.sdk.b2b.member
The [Member](Member.kt) interface provides methods for retrieving the current authenticated user.

You can choose to get the local representation of the user, without making a network request, with the `StytchB2BClient.member.getSync()` method.

If you want to get the freshest representation of the user from the Stytch servers, use the `StytchB2BClient.member.get()` method.

Call the `StytchB2BClient.member.onChange()` method with a callback to be triggered on changes to the underlying member. Alternatively, you can listen directly to the flow using the `StytchB2BClient.member.onChange` StateFlow.
