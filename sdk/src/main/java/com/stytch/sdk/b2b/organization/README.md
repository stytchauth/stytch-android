# Package com.stytch.sdk.b2b.organization
The [Organization](Organization.kt) interface provides methods for retrieving, updating, and deleting the current authenticated user's organization and creating, updating, deleting, reactivating, and searching an organizations members

Call the `StytchB2BClient.organization.get()` method to retrieve the members current organization. This request will make a network call and return the freshest information.

Call the `StytchB2BClient.organization.getSync()` method to retrieve the members current organization. This request will not make a network call and returns the cached information.

Call the `StytchB2BClient.organization.update()` method to update the members current organization.

Call the `StytchB2BClient.organization.delete()` method to delete the members current organization. All Members of the Organization will also be deleted.

Call the `StytchB2BClient.organization.members.create()` method to create a new member in an organization.

Call the `StytchB2BClient.organization.members.update()` method to update a specific member from an organization.

Call the `StytchB2BClient.organization.members.delete()` method to delete a specific member from an organization.

Call the `StytchB2BClient.organization.members.reactivate()` method to reactivate a specific member from an organization.

Call the `StytchB2BClient.organization.members.deleteMemberAuthenticationFactor()` method to delete an authentication factor from a member of the organization.

Call the `StytchB2BClient.organization.members.search()` method to search an organizations members.
