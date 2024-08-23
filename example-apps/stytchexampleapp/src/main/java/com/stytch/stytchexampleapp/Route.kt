package com.stytch.stytchexampleapp

internal sealed class Route(
    val route: String,
) {
    object Loading : Route("loading")

    object Login : Route("login")

    object Profile : Route("profile")
}
