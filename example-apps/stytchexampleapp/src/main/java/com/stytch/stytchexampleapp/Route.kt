package com.stytch.stytchexampleapp

internal sealed class Route(
    val route: String,
) {
    data object Loading : Route("loading")

    data object Login : Route("login")

    data object Profile : Route("profile")
}
