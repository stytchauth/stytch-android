# Package com.stytch.sdk.b2b.discovery
The [Discovery](Discovery.kt) interface provides methods for discovering a member's available organizations, creating organizations, and exchanging sessions between organizations.

The Discovery product lets End Users discover and log in to Organizations they are a Member of, invited to, or eligible to join.

Unlike our other B2B products, Discovery allows End Users to authenticate without specifying an Organization in advance. This is done via a Discovery Magic Link flow. After an End User is authenticated, an Intermediate Session is returned along with a list of associated Organizations.

The End User can then authenticate to the desired Organization by passing the Intermediate Session and organization_id. End users can even create a new Organization instead of joining or logging in to an existing one.

Call the `StytchB2BClient.discovery.organizations()` method to find a member's available organizations.

Call the `StytchB2BClient.discovery.exchangeSession()` method to exchange a session between organizations.

Call the `StytchB2BClient.discovery.create()` method to create a new organization.